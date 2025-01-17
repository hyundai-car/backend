package com.myme.mycarforme.domains.car.dto;

import lombok.Builder;

@Builder
public record ComparisonGraphItemDto(
        Double mmScoreNorm,
        Double accidentCountNorm,
        Double initialRegistrationNorm,
        Double mileageNorm,
        Double fuelEfficiencyNorm
) {
}
