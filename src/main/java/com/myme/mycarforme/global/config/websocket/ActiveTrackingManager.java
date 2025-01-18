package com.myme.mycarforme.global.config.websocket;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class ActiveTrackingManager {
    private final ConcurrentHashMap<String, String> activeTrackingMap = new ConcurrentHashMap<>(); // userId -> trackingCode

    public void addTracking(String userId, String trackingCode) {
        activeTrackingMap.put(userId, trackingCode);
    }

    public void removeTracking(String userId) {
        activeTrackingMap.remove(userId);
    }

    public boolean isActive(String userId, String trackingCode) {
        return activeTrackingMap.get(userId).equals(trackingCode);
    }

    public String getTrackingCode(String userId) {
        return activeTrackingMap.get(userId);
    }
}
