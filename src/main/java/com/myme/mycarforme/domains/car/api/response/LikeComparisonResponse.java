package com.myme.mycarforme.domains.car.api.response;

import com.myme.mycarforme.domains.car.domain.Car;
import com.myme.mycarforme.domains.car.dto.BestCarComparisonDto;
import com.myme.mycarforme.domains.car.dto.ComparisonAvgDto;

import java.util.List;

public record LikeComparisonResponse(
        BestCarComparisonDto bestCar,
        ComparisonAvgDto comparisons,
        ComparisonGraphDto graph,
        List<Long> otherCarIds
) {
    public static LikeComparisonResponse of(Car car,
                                            Double mmScoreAvg,
                                            Double accidentCountAvg,
                                            String initialRegistrationAvg,
                                            Double mileageAvg,
                                            Double fuelEfficiencyAvg,
                                            List<Long> otherCarIds) {
        return new LikeComparisonResponse(
                BestCarComparisonDto.from(car),
                ComparisonAvgDto.builder()
                        .mmScoreAvg(mmScoreAvg)
                        .accidentCountAvg(accidentCountAvg)
                        .initialRegistrationAvg(initialRegistrationAvg)
                        .mileageAvg(mileageAvg)
                        .fuelEfficiencyAvg(fuelEfficiencyAvg)
                        .build(),
                otherCarIds
        );
    }
}
