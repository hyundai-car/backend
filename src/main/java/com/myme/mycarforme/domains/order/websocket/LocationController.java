package com.myme.mycarforme.domains.order.websocket;

import com.myme.mycarforme.domains.order.websocket.message.LocationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class LocationController {
    private final SimpMessagingTemplate messagingTemplate;

    // 관리자가 위치 업데이트시
    @MessageMapping("/delivery/location")
    public void updateLocation(@Payload LocationMessage location) {
        // 동적 경로 생성
        String destination = String.format("/sub/tracking/%s/location", location.getTrackingCode());

        // 메시지 전송
        messagingTemplate.convertAndSend(destination, location);
    }
}
