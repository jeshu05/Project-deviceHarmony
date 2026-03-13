/**
 * deviceHarmony — Secure Local Network File Transfer Server
 * Architecture:
 *   - Express HTTP for REST API + static files
 *   - WebSocket (ws) for real-time device communication & file relay
 *   - In-memory sessions: no disk persistence, session-scoped only
 *   - File chunks relay: secondary device → server → primary device
 */

'use strict';

const express    = require('express');
const { WebSocketServer } = require('ws');
const http       = require('http');
const { v4: uuidv4 } = require('uuid');
const os         = require('os');
const crypto     = require('crypto');
const path       = require('path');

//Server Setup

const app    = express();
const server = http.createServer(app);
const wss    = new WebSocketServer({ server, path: '/ws' });

app.use(express.json({ limit: '5mb' }));
app.use(express.static(path.join(__dirname, 'public')));

// n-Memory State 

/**
 * rooms: Map<roomCode, Room>
 * Room {
 *   code: string
 *   createdAt: number
 *   primaryDeviceId: string | null
 *   devices: Map<deviceId, Device>
 * }
 *
 * Device {
 *   id, name, type, os, sessionToken,
 *   isPrimary, connectedAt, status
 * }
 */
const rooms         = new Map();
const deviceSockets = new Map(); // deviceId → WebSocket

// ─── Helpers

function genRoomCode() {
  let code;
  do { code = Math.random().toString(36).substr(2, 6).toUpperCase(); }
  while (rooms.has(code));
  return code;
}

function genToken() {
  return crypto.randomBytes(32).toString('hex');
}

function sanitizeDevice(d) {
  const { sessionToken: _, ...safe } = d;
  return safe;
}

function getServerResources() {
  const total   = os.totalmem();
  const free    = os.freemem();
  const cpus    = os.cpus();
  const loadAvg = os.loadavg();
  return {
    memory : { total, free, used: total - free, pct: +((1 - free / total) * 100).toFixed(1) },
    cpu    : { count: cpus.length, model: cpus[0]?.model || 'Unknown', load: loadAvg[0] },
    platform: os.platform(),
    arch    : os.arch(),
    hostname: os.hostname(),
    uptime  : os.uptime(),
    timestamp: Date.now()
  };
}

function broadcastToRoom(roomCode, msg, excludeId = null) {
  const room = rooms.get(roomCode);
  if (!room) return;
  const payload = JSON.stringify(msg);
  room.devices.forEach((dev, id) => {
    if (id === excludeId) return;
    const ws = deviceSockets.get(id);
    if (ws && ws.readyState === 1) ws.send(payload);
  });
}

function sendTo(deviceId, msg) {
  const ws = deviceSockets.get(deviceId);
  if (ws && ws.readyState === 1) ws.send(JSON.stringify(msg));
}

function validateSession(roomCode, deviceId, sessionToken) {
  const room   = rooms.get(roomCode);
  if (!room) return null;
  const device = room.devices.get(deviceId);
  if (!device || device.sessionToken !== sessionToken) return null;
  return { room, device };
}

// ─── REST API 

// Create a new room — the creating device becomes the first Primary
app.post('/api/room/create', (req, res) => {
  const { deviceName, deviceType, deviceOS } = req.body;
  if (!deviceName) return res.status(400).json({ error: 'deviceName required' });

  const code      = genRoomCode();
  const deviceId  = uuidv4();
  const token     = genToken();

  const device = {
    id: deviceId, name: deviceName,
    type: deviceType || 'desktop', os: deviceOS || 'unknown',
    sessionToken: token, isPrimary: true,
    connectedAt: Date.now(), status: 'online'
  };

  const room = {
    code, createdAt: Date.now(),
    primaryDeviceId: deviceId,
    devices: new Map([[deviceId, device]])
  };

  rooms.set(code, room);
  console.log(`[ROOM] Created ${code} by "${deviceName}"`);

  res.json({ success: true, roomCode: code, deviceId, sessionToken: token });
});

// Join an existing room
app.post('/api/room/join', (req, res) => {
  const { roomCode, deviceName, deviceType, deviceOS } = req.body;
  if (!roomCode || !deviceName) return res.status(400).json({ error: 'roomCode + deviceName required' });

  const code = roomCode.toUpperCase().trim();
  const room = rooms.get(code);
  if (!room) return res.status(404).json({ error: 'Room not found. Check the code and try again.' });

  const deviceId = uuidv4();
  const token    = genToken();

  const device = {
    id: deviceId, name: deviceName,
    type: deviceType || 'desktop', os: deviceOS || 'unknown',
    sessionToken: token, isPrimary: false,
    connectedAt: Date.now(), status: 'online'
  };

  room.devices.set(deviceId, device);
  console.log(`[ROOM] "${deviceName}" joined ${code}`);

  // Notify existing devices
  broadcastToRoom(code, { type: 'device_joined', device: sanitizeDevice(device) }, deviceId);

  const deviceList = Array.from(room.devices.values()).map(sanitizeDevice);
  res.json({ success: true, roomCode: code, deviceId, sessionToken: token,
             primaryDeviceId: room.primaryDeviceId, devices: deviceList });
});

// Get room state (used on reconnect / refresh)
app.get('/api/room/:code', (req, res) => {
  const room = rooms.get(req.params.code.toUpperCase());
  if (!room) return res.status(404).json({ error: 'Room not found' });

  const devices = Array.from(room.devices.values()).map(sanitizeDevice);
  res.json({ success: true, room: { code: room.code, createdAt: room.createdAt,
    primaryDeviceId: room.primaryDeviceId, devices } });
});

// Transfer Primary designation
app.post('/api/device/set-primary', (req, res) => {
  const { roomCode, deviceId, sessionToken, targetDeviceId } = req.body;
  const v = validateSession(roomCode, deviceId, sessionToken);
  if (!v) return res.status(403).json({ error: 'Unauthorized' });

  const { room } = v;
  const target = room.devices.get(targetDeviceId || deviceId);
  if (!target) return res.status(404).json({ error: 'Target device not found' });

  // Demote previous primary
  if (room.primaryDeviceId) {
    const prev = room.devices.get(room.primaryDeviceId);
    if (prev) prev.isPrimary = false;
  }
  target.isPrimary     = true;
  room.primaryDeviceId = target.id;

  broadcastToRoom(roomCode, { type: 'primary_changed', primaryDeviceId: target.id });
  console.log(`[ROOM] ${roomCode} primary → "${target.name}"`);
  res.json({ success: true });
});

// Remove a device (self leave)
app.post('/api/device/leave', (req, res) => {
  const { roomCode, deviceId, sessionToken } = req.body;
  const v = validateSession(roomCode, deviceId, sessionToken);
  if (!v) return res.status(403).json({ error: 'Unauthorized' });

  const { room, device } = v;
  room.devices.delete(deviceId);
  deviceSockets.delete(deviceId);

  broadcastToRoom(roomCode, { type: 'device_left', deviceId, deviceName: device.name });
  console.log(`[ROOM] "${device.name}" left ${roomCode}`);

  // Clean up empty rooms
  if (room.devices.size === 0) {
    rooms.delete(roomCode);
    console.log(`[ROOM] ${roomCode} dissolved (empty)`);
  } else if (room.primaryDeviceId === deviceId) {
    // Auto-assign first remaining device as primary
    const [newPrimaryId, newPrimary] = room.devices.entries().next().value;
    newPrimary.isPrimary    = true;
    room.primaryDeviceId    = newPrimaryId;
    broadcastToRoom(roomCode, { type: 'primary_changed', primaryDeviceId: newPrimaryId });
  }

  res.json({ success: true });
});

// Server resource check endpoint
app.get('/api/resources', (req, res) => {
  res.json(getServerResources());
});

// Health / server info
app.get('/api/health', (req, res) => {
  res.json({
    status : 'ok',
    rooms  : rooms.size,
    devices: Array.from(rooms.values()).reduce((n, r) => n + r.devices.size, 0),
    uptime : process.uptime(),
    ts     : Date.now()
  });
});

// ─── WebSocket Handler

wss.on('connection', (ws) => {
  let myDeviceId = null;
  let myRoomCode = null;

  ws.on('message', (raw) => {
    try {
      const msg = JSON.parse(raw.toString());

      // ── Authenticate ──
      if (msg.type === 'auth') {
        const v = validateSession(msg.roomCode, msg.deviceId, msg.sessionToken);
        if (!v) { ws.send(JSON.stringify({ type: 'error', code: 'AUTH_FAILED' })); return; }

        myDeviceId = msg.deviceId;
        myRoomCode = msg.roomCode;
        v.device.status = 'online';
        deviceSockets.set(myDeviceId, ws);

        const devices = Array.from(v.room.devices.values()).map(sanitizeDevice);
        ws.send(JSON.stringify({ type: 'auth_ok', devices,
          primaryDeviceId: v.room.primaryDeviceId, resources: getServerResources() }));

        // Notify others this device is back online
        broadcastToRoom(myRoomCode, { type: 'device_online', deviceId: myDeviceId }, myDeviceId);
        return;
      }

      if (!myDeviceId) {
        ws.send(JSON.stringify({ type: 'error', code: 'NOT_AUTHED' }));
        return;
      }

      route(ws, msg, myDeviceId, myRoomCode);

    } catch (e) {
      console.error('[WS] parse error:', e.message);
    }
  });

  ws.on('close', () => {
    if (!myDeviceId || !myRoomCode) return;
    const room   = rooms.get(myRoomCode);
    const device = room?.devices.get(myDeviceId);
    if (device) device.status = 'offline';
    deviceSockets.delete(myDeviceId);
    broadcastToRoom(myRoomCode, { type: 'device_offline', deviceId: myDeviceId }, myDeviceId);
    console.log(`[WS] "${device?.name}" disconnected from ${myRoomCode}`);
  });

  ws.on('error', (e) => console.error('[WS] error:', e.message));
});

//  Message Router 

function route(ws, msg, fromId, roomCode) {
  switch (msg.type) {

    // File list: primary → secondary (request) and secondary → primary (response)
    case 'file_list_request':
    case 'file_list_response':
    // Transfer handshake
    case 'transfer_request':
    case 'transfer_accept':
    case 'transfer_reject':
    case 'transfer_progress':
    case 'transfer_complete':
    case 'transfer_error':
    case 'transfer_cancel':
    // Resource check relay
    case 'resource_check_request':
    case 'resource_check_response': {
      const target = msg.targetDeviceId;
      if (!target) { ws.send(JSON.stringify({ type: 'error', code: 'NO_TARGET' })); return; }
      sendTo(target, { ...msg, fromDeviceId: fromId });
      break;
    }

    // File chunk relay (hot path — just forward binary-safe JSON)
    case 'file_chunk': {
      const target = msg.targetDeviceId;
      if (target) sendTo(target, msg);
      break;
    }

    // Broadcast a message to the whole room
    case 'room_broadcast': {
      broadcastToRoom(roomCode, { ...msg, fromDeviceId: fromId }, fromId);
      break;
    }

    case 'ping':
      ws.send(JSON.stringify({ type: 'pong', ts: Date.now() }));
      break;

    // Server-side resource check (no relay)
    case 'server_resources':
      ws.send(JSON.stringify({ type: 'server_resources', resources: getServerResources() }));
      break;

    default:
      console.warn(`[WS] Unknown message type: ${msg.type}`);
  }
}

// ─── Bootstrap 
function getLocalIP() {
  const ifaces = os.networkInterfaces();
  for (const name of Object.keys(ifaces)) {
    for (const iface of ifaces[name]) {
      if (iface.family === 'IPv4' && !iface.internal) return iface.address;
    }
  }
  return 'localhost';
}

// Periodic cleanup: remove empty rooms older than 2 hours
setInterval(() => {
  const cutoff = Date.now() - 2 * 60 * 60 * 1000;
  rooms.forEach((room, code) => {
    if (room.devices.size === 0 && room.createdAt < cutoff) {
      rooms.delete(code);
      console.log(`[GC] Cleaned empty room ${code}`);
    }
  });
}, 30 * 60 * 1000);

const PORT = process.env.PORT || 3000;
server.listen(PORT, '0.0.0.0', () => {
  const ip = getLocalIP();
  console.log('\n');
  console.log('  ██████╗ ██╗  ██╗ █████╗ ██████╗ ███╗   ███╗ ██████╗ ███╗   ██╗██╗   ██╗');
  console.log('  ██╔══██╗██║  ██║██╔══██╗██╔══██╗████╗ ████║██╔═══██╗████╗  ██║╚██╗ ██╔╝');
  console.log('  █████████║███████║██████╔╝██╔████╔██║██║   ██║██╔██╗ ██║ ╚████╔╝ ');
  console.log('  ██║  ██║██╔══██║██╔══██║██╔══██╗██║╚██╔╝██║██║   ██║██║╚██╗██║  ╚██╔╝  ');
  console.log('  ██████╔╝██║  ██║██║  ██║██║  ██║██║ ╚═╝ ██║╚██████╔╝██║ ╚████║   ██║   ');
  console.log('  ╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝╚═╝     ╚═╝ ╚═════╝ ╚═╝  ╚═══╝   ╚═╝   ');
  console.log('\n  Secure Local Device Ecosystem\n');
  console.log(`  ┌─────────────────────────────────────────┐`);
  console.log(`  │  Local    →  http://localhost:${PORT}       │`);
  console.log(`  │  Network  →  http://${ip}:${PORT}     │`);
  console.log(`  └─────────────────────────────────────────┘\n`);
  console.log('  Open the Network URL on any device to join.\n');
  console.log('Check IP with ipconfig if the link doesnt work!!\n');
});
