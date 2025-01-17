package com.myme.mycarforme.domains.car.dto;

import com.myme.mycarforme.domains.car.domain.Car;

public record RecommendDto(CategoryWeights weights, CarScore scores) {
    public static RecommendDto of(CategoryWeights weights, CarScore scores) {
        return new RecommendDto(weights, scores);
    }

    public record CategoryWeights(
            double efficiencyWeight,
            double typeWeight,
            double fuelWeight,
            double displacementWeight,
            double repairWeight,
            double preferredTypeWeight
    ) {
        public static CategoryWeights of(
                double efficiencyWeight,
                double typeWeight,
                double fuelWeight,
                double displacementWeight,
                double repairWeight,
                double preferredTypeWeight
        ) {
            return new CategoryWeights(
                    efficiencyWeight,
                    typeWeight,
                    fuelWeight,
                    displacementWeight,
                    repairWeight ,
                    preferredTypeWeight
            );
        }
    }

    public record CarScore(
            Car car,
            double efficiencyScore,
            double typeScore,
            double fuelScore,
            double displacementScore,
            double repairScore,
            double preferredTypeScore,
            double finalTypeScore,
            double totalScore
    ) {
        public static CarScore of(
                Car car,
                double efficiencyScore,
                double typeScore,
                double fuelScore,
                double displacementScore,
                double repairScore,
                double preferredTypeScore,
                double finalTypeScore,
                double totalScore
        ) {
            return new CarScore(
                    car,
                    efficiencyScore,
                    typeScore,
                    fuelScore,
                    displacementScore,
                    repairScore,
                    preferredTypeScore,
                    finalTypeScore,
                    totalScore
            );
        }
    }

}
