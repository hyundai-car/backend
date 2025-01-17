package com.myme.mycarforme.domains.order.api.response;

import com.myme.mycarforme.domains.order.dto.StatusHistoriesDto;
import java.time.LocalDateTime;

public record OrderStatusResponse(
        Long carId,
        String paymentDeliveryStatus,
        StatusHistoriesDto statusHistories,
        String createdAt,
        String updatedAt
) {
    public static OrderStatusResponse of(
            Long carId,
            Integer paymentDeliveryStatus,
            LocalDateTime contractedAt,
            LocalDateTime paidAt,
            LocalDateTime deliveryStartedAt,
            LocalDateTime deliveryEndedAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        String paymentDeliveryStatusString = switch (paymentDeliveryStatus) {
            case 1 -> "CONTRACTED";
            case 2 -> "PAID";
            case 3 -> "DELIVERING";
            case 4 -> "DELIVERED";
            default -> "UNKNOWN";
        };

        return new OrderStatusResponse(
                carId,
                paymentDeliveryStatusString,
                StatusHistoriesDto.builder()
                        .contractedAt(contractedAt.toString())
                        .paidAt(paidAt.toString())
                        .deliveryStartedAt(deliveryStartedAt.toString())
                        .deliveryEndedAt(deliveryEndedAt.toString())
                        .build(),
                createdAt.toString(),
                updatedAt.toString()
        );
    }
}
