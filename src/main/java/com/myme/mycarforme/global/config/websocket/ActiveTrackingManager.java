package com.myme.mycarforme.global.config.websocket;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class ActiveTrackingManager {
    private final Set<String> activeTrackingList = ConcurrentHashMap.newKeySet();

    public void addTracking(String trackingCode) {
        activeTrackingList.add(trackingCode);
    }

    public void removeTracking(String trackingCode) {
        activeTrackingList.remove(trackingCode);
    }

    public boolean isActive(String trackingCode) {
        return activeTrackingList.contains(trackingCode);
    }
}
