package com.myme.mycarforme.global.config.websocket;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class ActiveTrackingManager {
    private final ConcurrentHashMap<String, String> activeSessionMap = new ConcurrentHashMap<>(); // sessionId -> userId
    private final ConcurrentHashMap<String, String> activeTrackingMap = new ConcurrentHashMap<>(); // userId -> trackingCode

    public void addTracking(String sessionId, String userId, String trackingCode) {
        activeSessionMap.put(sessionId, userId);
        activeTrackingMap.put(userId, trackingCode);
    }

    public void removeTracking(String sessionId) {
        activeTrackingMap.remove(activeSessionMap.get(sessionId));
        activeSessionMap.remove(sessionId);
    }

    public boolean isActiveSession(String sessionId) {
        return activeSessionMap.containsKey(sessionId);
    }

    public boolean isActive(String userId, String trackingCode) {
        return activeTrackingMap.get(userId).equals(trackingCode);
    }

    public Optional<String> getTrackingCodeWithSessionId(String sessionId) {
        if(activeSessionMap.containsKey(sessionId)) {
            return Optional.ofNullable(activeTrackingMap.get(activeSessionMap.get(sessionId)));
        } else {
            return Optional.empty();
        }
    }

    public Optional<String> getTrackingCode(String userId) {
        return Optional.ofNullable(activeTrackingMap.get(userId));
    }
}
