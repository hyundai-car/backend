package com.myme.mycarforme.domains.order.dto;

import lombok.Builder;

@Builder
public record PaymentInfoDto(
        String paymentType,
        Long paidPrice
) {
}
