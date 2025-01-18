package com.myme.mycarforme.domains.order.event;

import com.myme.mycarforme.domains.order.service.OrderService;
import com.myme.mycarforme.global.config.websocket.ActiveTrackingManager;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {
    private final ActiveTrackingManager activeTrackingManager;
    private final OrderService orderService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());

        Message<?> connectMessage = headers.getMessageHeaders().get("simpConnectMessage", Message.class);
        if (connectMessage != null) {
            @SuppressWarnings("unchecked")
            Map<String, List<String>> nativeHeaders =
                    (Map<String, List<String>>) connectMessage.getHeaders().get("nativeHeaders");
            if (nativeHeaders != null) {
                String sessionId = headers.getSessionId();
                String clientType = nativeHeaders.get("clientType").get(0);
                String userId = nativeHeaders.get("userId").get(0);

                if (userId != null && Objects.equals(clientType, "pub")) {
                    log.info("새로운 탁송 시작. [sessionId: {}, userId: {}]", sessionId, userId);
                    activeTrackingManager.addTracking(sessionId, userId);

                    orderService.updateDeliveryStartedStatus(userId);
                }
            }
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());

        String sessionId = headers.getSessionId();

        if (sessionId != null && activeTrackingManager.isActiveSession(sessionId)) {
            String userId = activeTrackingManager.getUserId(sessionId);

            if (userId != null) {
                log.info("탁송 종료. [sessionId: {}, userId: {}]", sessionId, userId);
                activeTrackingManager.removeTracking(sessionId);

                orderService.updateDeliveryEndedStatus(userId);
            }
        }
    }
}
