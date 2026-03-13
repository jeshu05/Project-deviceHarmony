// AddDeviceServlet.java
// Location: src/com/deviceharmony/web/AddDeviceServlet.java
package com.deviceharmony.web;

import com.deviceharmony.database.DatabaseManager;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import com.google.gson.*;

public class AddDeviceServlet extends HttpServlet {
    private DatabaseManager dbManager;
    private Gson gson = new Gson();

    public AddDeviceServlet(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setStatus(200);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");

        BufferedReader reader = req.getReader();
        JsonObject json = gson.fromJson(reader, JsonObject.class);

        String deviceName = json.has("deviceName") && !json.get("deviceName").isJsonNull()
            ? json.get("deviceName").getAsString().trim() : "";
        String deviceType = json.has("deviceType") && !json.get("deviceType").isJsonNull()
            ? json.get("deviceType").getAsString().trim() : "";

        if (deviceName.isEmpty()) {
            resp.setStatus(400);
            resp.getWriter().write(gson.toJson(errorMap("Device name is required")));
            return;
        }

        String ipAddress = null;
        Integer port = null;
        String sharePath = null;

        switch (deviceType) {
            case "agent":
                ipAddress = json.has("ipAddress") && !json.get("ipAddress").isJsonNull()
                    ? json.get("ipAddress").getAsString().trim() : "";
                if (ipAddress.isEmpty()) {
                    resp.setStatus(400);
                    resp.getWriter().write(gson.toJson(errorMap("IP address is required for agent type")));
                    return;
                }
                port = json.has("port") && !json.get("port").isJsonNull()
                    ? json.get("port").getAsInt() : 9877;
                break;
            case "share":
                sharePath = json.has("sharePath") && !json.get("sharePath").isJsonNull()
                    ? json.get("sharePath").getAsString().trim() : "";
                if (sharePath.isEmpty()) {
                    resp.setStatus(400);
                    resp.getWriter().write(gson.toJson(errorMap("Share path is required for share type")));
                    return;
                }
                break;
            case "manual":
                sharePath = json.has("sharePath") && !json.get("sharePath").isJsonNull()
                    ? json.get("sharePath").getAsString().trim() : "";
                if (sharePath.isEmpty()) {
                    resp.setStatus(400);
                    resp.getWriter().write(gson.toJson(errorMap("Local directory path is required for manual type")));
                    return;
                }
                break;
            default:
                resp.setStatus(400);
                resp.getWriter().write(gson.toJson(errorMap("Unknown device type: " + deviceType)));
                return;
        }

        try {
            String deviceId = UUID.randomUUID().toString();
            dbManager.registerDevice(deviceId, deviceName, deviceType, ipAddress, port, sharePath);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("deviceId", deviceId);
            resp.getWriter().write(gson.toJson(response));
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write(gson.toJson(errorMap(e.getMessage())));
        }
    }

    private Map<String, Object> errorMap(String message) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", false);
        map.put("error", message);
        return map;
    }
}
