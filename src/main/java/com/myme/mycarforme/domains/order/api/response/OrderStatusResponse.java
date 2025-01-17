package com.myme.mycarforme.domains.order.api.response;

import com.myme.mycarforme.domains.order.constant.OrderStatus;
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
            case 1 -> OrderStatus.CONTRACTED.getStatus();
            case 2 -> OrderStatus.PAID.getStatus();
            case 3 -> OrderStatus.DELIVERING.getStatus();
            case 4 -> OrderStatus.DELIVERED.getStatus();
            default -> OrderStatus.UNKNOWN.getStatus();
        };

        return new OrderStatusResponse(
                carId,
                paymentDeliveryStatusString,
                StatusHistoriesDto.builder()
                        .contractedAt(contractedAt != null ? contractedAt.toString() : null)
                        .paidAt(paidAt != null ? paidAt.toString() : null)
                        .deliveryStartedAt(deliveryStartedAt != null ? deliveryStartedAt.toString() : null)
                        .deliveryEndedAt(deliveryEndedAt != null ? deliveryEndedAt.toString() : null)
                        .build(),
                createdAt.toString(),
                updatedAt.toString()
        );
    }
}
