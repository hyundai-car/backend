package com.myme.mycarforme.global.config.websocket;

import com.myme.mycarforme.global.config.keycloak.KeycloakJwtAuthenticationConverter;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthChannelInterceptor implements ChannelInterceptor {
    private final JwtDecoder jwtDecoder;
    private final KeycloakJwtAuthenticationConverter jwtAuthenticationConverter;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(
                message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(Objects.requireNonNull(accessor).getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            log.debug("WebSocket Connection attempt with Authorization header: {}", authHeader);

            try {
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    Jwt jwt = jwtDecoder.decode(token);
                    AbstractAuthenticationToken authentication = jwtAuthenticationConverter.convert(jwt);
                    accessor.setUser(authentication);
                } else {
                    log.error("No Authorization header in WebSocket connection");
                    throw new MessagingException("No Authorization header");
                }
            } catch (Exception e) {
                log.error("WebSocket authentication failed", e);
                throw new MessagingException("Authentication failed");
            }
        }
        return message;
    }
}
