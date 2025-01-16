package com.myme.mycarforme.domains.car.dto;

import lombok.Builder;

@Builder
public record ComparisonAvgDto(
              Double mmScoreAvg,
              Double accidentCountAvg,
              String initialRegistrationAvg,
              Double mileageAvg,
              Double fuelEfficiencyAvg
) {
}
