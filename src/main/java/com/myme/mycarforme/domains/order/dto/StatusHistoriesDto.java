package com.myme.mycarforme.domains.order.dto;

import lombok.Builder;

@Builder
public record StatusHistoriesDto(
        String contractedAt,
        String paidAt,
        String deliveryStartedAt,
        String deliveryEndedAt
) {
}
