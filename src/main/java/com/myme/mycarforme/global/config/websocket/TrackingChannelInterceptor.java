package com.myme.mycarforme.global.config.websocket;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrackingChannelInterceptor implements ChannelInterceptor {
    private final ActiveTrackingManager activeTrackingManager;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(
                message, StompHeaderAccessor.class);

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String destination = accessor.getDestination();
            String trackingCode = extractTrackingCode(destination);
            String userId = accessor.getUser().getName();

            if (!activeTrackingManager.isActive(userId, trackingCode)) {
                throw new MessageDeliveryException("Inactive tracking code: " + trackingCode);
            }
        }

        return message;
    }
    private String extractTrackingCode(String destination) {
        String[] parts = destination.split("/");
        if (parts.length >= 4) {
            return parts[3];
        }
        throw new IllegalArgumentException("TrackingCode를 파싱할 수 없습니다 : " + destination);
    }
}
