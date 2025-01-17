package com.myme.mycarforme.domains.car.dto;

import lombok.Builder;

@Builder
public record ComparisonGraphDto(
        ComparisonGraphItemDto best,
        ComparisonGraphItemDto avg
) {
}
