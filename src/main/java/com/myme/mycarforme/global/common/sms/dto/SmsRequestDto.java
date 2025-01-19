package com.myme.mycarforme.global.common.sms.dto;

import lombok.Builder;

@Builder
public record SmsRequestDto(
        String phone,
        String message
) {
}
