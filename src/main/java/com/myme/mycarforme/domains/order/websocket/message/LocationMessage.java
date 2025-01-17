package com.myme.mycarforme.domains.order.websocket.message;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class LocationMessage {
    private String trackingCode;
    private Double latitude;
    private Double longitude;
    private LocalDateTime timestamp;
}
