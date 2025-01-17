package com.myme.mycarforme.domains.car.api.response;

import com.myme.mycarforme.domains.car.domain.Car;
import com.myme.mycarforme.domains.car.dto.BestCarComparisonDto;
import com.myme.mycarforme.domains.car.dto.ComparisonAvgDto;

import com.myme.mycarforme.domains.car.dto.ComparisonGraphDto;
import com.myme.mycarforme.domains.car.dto.ComparisonGraphItemDto;
import com.myme.mycarforme.global.util.helper.DateFormatHelper;
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
                                            Double bestMmScoreNorm,
                                            Double bestAccidentCountNorm,
                                            Double bestInitialRegistrationNorm,
                                            Double bestMileageNorm,
                                            Double bestFuelEfficiencyNorm,
                                            Double avgMmScoreNorm,
                                            Double avgAccidentCountNorm,
                                            Double avgInitialRegistrationNorm,
                                            Double avgMileageNorm,
                                            Double avgFuelEfficiencyNorm,
                                            List<Long> otherCarIds) {
        return new LikeComparisonResponse(
                BestCarComparisonDto.from(car),
                ComparisonAvgDto.builder()
                        .mmScoreAvg(mmScoreAvg)
                        .accidentCountAvg(accidentCountAvg)
                        .initialRegistrationAvg(DateFormatHelper.toKoreanDateString(initialRegistrationAvg))
                        .mileageAvg(mileageAvg)
                        .fuelEfficiencyAvg(fuelEfficiencyAvg)
                        .build(),
                ComparisonGraphDto.builder()
                        .best(
                                ComparisonGraphItemDto.builder()
                                        .mmScoreNorm(bestMmScoreNorm)
                                        .accidentCountNorm(bestAccidentCountNorm)
                                        .initialRegistrationNorm(bestInitialRegistrationNorm)
                                        .mileageNorm(bestMileageNorm)
                                        .fuelEfficiencyNorm(bestFuelEfficiencyNorm)
                                        .build()
                        )
                        .avg(
                                ComparisonGraphItemDto.builder()
                                        .mmScoreNorm(avgMmScoreNorm)
                                        .accidentCountNorm(avgAccidentCountNorm)
                                        .initialRegistrationNorm(avgInitialRegistrationNorm)
                                        .mileageNorm(avgMileageNorm)
                                        .fuelEfficiencyNorm(avgFuelEfficiencyNorm)
                                        .build()
                        )
                        .build(),
                otherCarIds
        );
    }
}
