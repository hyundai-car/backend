package com.myme.mycarforme.domains.admin.dto;

import lombok.Builder;

@Builder
public record CarFormDto(
        Long id,
        String name,
        String brand,
        Long price,
        Integer year,
        String description
) {
}
