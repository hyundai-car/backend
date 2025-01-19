package com.myme.mycarforme.global.config.websocket;

import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class ActiveTrackingManager {
    private final ConcurrentHashMap<String, String> activeSessionMap = new ConcurrentHashMap<>(); // sessionId -> userId

    public void addTracking(String sessionId, String userId) {
        activeSessionMap.put(sessionId, userId);
    }

    public void removeTracking(String sessionId) {
        activeSessionMap.remove(sessionId);
    }

    public boolean isActiveSession(String sessionId) {
        return activeSessionMap.containsKey(sessionId);
    }

    public boolean isActive(String userId) {
        return activeSessionMap.containsValue(userId);
    }

    public String getUserId(String sessionId) {
        return activeSessionMap.get(sessionId);
    }
}
