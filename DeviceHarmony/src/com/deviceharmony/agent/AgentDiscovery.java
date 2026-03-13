// AgentDiscovery.java - DISCOVERS CLIENT AGENTS
package com.deviceharmony.agent;

import com.deviceharmony.database.DatabaseManager;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class AgentDiscovery {
    private static final int DISCOVERY_PORT = 9876;
    private static final String MULTICAST_GROUP = "230.0.0.0";
    private final DatabaseManager dbManager;
    private MulticastSocket socket;
    private InetAddress group;
    private volatile boolean running = false;
    private ScheduledExecutorService heartbeatChecker;
    private Thread listenerThread;
    private Map<String, AgentInfo> discoveredAgents = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Long> lastSeen = new ConcurrentHashMap<>();

    public AgentDiscovery(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public void start() throws Exception {
        socket = new MulticastSocket(DISCOVERY_PORT);
        socket.setSoTimeout(2000);
        group = InetAddress.getByName(MULTICAST_GROUP);
        socket.joinGroup(group);
        running = true;

        // Start named daemon listener thread for agent broadcasts
        listenerThread = new Thread(this::listenForAgents, "agent-discovery-listener");
        listenerThread.setDaemon(true);
        listenerThread.start();

        // Check agent health every 15 seconds using a named daemon thread
        heartbeatChecker = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "agent-heartbeat-checker");
            t.setDaemon(true);
            return t;
        });
        heartbeatChecker.scheduleAtFixedRate(this::checkAgentHealth, 15, 15, TimeUnit.SECONDS);
    }

    private void listenForAgents() {
        byte[] buffer = new byte[1024];
        while (running) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());
                if (message.startsWith("DEVICEHARMONY_AGENT|")) {
                    String[] parts = message.split("\\|");
                    if (parts.length >= 5) {
                        String agentId = parts[1];
                        String agentName = parts[2];
                        String agentIp = parts[3];
                        int agentPort = Integer.parseInt(parts[4]);

                        // Update last-seen timestamp
                        lastSeen.put(agentId, System.currentTimeMillis());

                        AgentInfo info = new AgentInfo(agentId, agentName, agentIp, agentPort);
                        discoveredAgents.put(agentId, info);

                        // Register in database
                        dbManager.registerDevice(agentId, agentName, "agent", agentIp, agentPort, null);

                        // Fire non-critical daemon thread to fetch and store storage info
                        Thread storageThread = new Thread(
                            () -> fetchStorageInfo(agentId, agentIp, agentPort),
                            "agent-storage-" + agentId);
                        storageThread.setDaemon(true);
                        storageThread.start();
                    }
                }
            } catch (SocketTimeoutException e) {
                // Expected: timeout lets us re-check the running flag without blocking stop()
                continue;
            } catch (Exception e) {
                if (running) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void fetchStorageInfo(String agentId, String agentIp, int agentPort) {
        try {
            URL url = new URL("http://" + agentIp + ":" + agentPort + "/files/info");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();

                long totalStorage = extractLong(sb.toString(), "totalStorage");
                long availableStorage = extractLong(sb.toString(), "availableStorage");
                if (totalStorage > 0) {
                    dbManager.updateDeviceStorage(agentId, totalStorage, availableStorage);
                }
            }
            conn.disconnect();
        } catch (Exception e) {
            // Non-critical: ignore failures silently
        }
    }

    private long extractLong(String json, String key) {
        try {
            String pattern = "\"" + key + "\"";
            int idx = json.indexOf(pattern);
            if (idx < 0) return 0;
            idx = json.indexOf(':', idx + pattern.length());
            if (idx < 0) return 0;
            idx++;
            while (idx < json.length() && Character.isWhitespace(json.charAt(idx))) idx++;
            int start = idx;
            while (idx < json.length() && (Character.isDigit(json.charAt(idx)) || json.charAt(idx) == '-')) idx++;
            if (start == idx) return 0;
            return Long.parseLong(json.substring(start, idx));
        } catch (Exception e) {
            return 0;
        }
    }

    private void checkAgentHealth() {
        try {
            long now = System.currentTimeMillis();
            for (Map.Entry<String, Long> entry : lastSeen.entrySet()) {
                String agentId = entry.getKey();
                long seen = entry.getValue();
                if (now - seen > 20000) {
                    try {
                        dbManager.updateDeviceStatus(agentId, "offline");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    discoveredAgents.remove(agentId);
                    lastSeen.remove(agentId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, AgentInfo> getDiscoveredAgents() {
        return new HashMap<>(discoveredAgents);
    }

    public void stop() {
        running = false;
        if (heartbeatChecker != null) {
            heartbeatChecker.shutdownNow();
        }
        if (listenerThread != null) {
            listenerThread.interrupt();
        }
        try {
            if (socket != null && !socket.isClosed()) {
                socket.leaveGroup(group);
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class AgentInfo {
        public final String agentId;
        public final String agentName;
        public final String ipAddress;
        public final int port;
        public long lastSeen;

        public AgentInfo(String agentId, String agentName, String ipAddress, int port) {
            this.agentId = agentId;
            this.agentName = agentName;
            this.ipAddress = ipAddress;
            this.port = port;
            this.lastSeen = System.currentTimeMillis();
        }
    }
}
	