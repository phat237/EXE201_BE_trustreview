package com.trustreview.trustreview.Service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service

public class ClientService {
    public Map<String, String> getClientInfo(HttpServletRequest request) {
        Map<String, String> clientInfo = new HashMap<>();

        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        if ("0:0:0:0:0:0:0:1".equals(ipAddress) || "::1".equals(ipAddress)) {
            ipAddress = "127.0.0.1";
        }
        clientInfo.put("ipAddress", ipAddress);

        String userAgent = request.getHeader("User-Agent");
        clientInfo.put("userAgent", userAgent);

        return clientInfo;
    }
}
