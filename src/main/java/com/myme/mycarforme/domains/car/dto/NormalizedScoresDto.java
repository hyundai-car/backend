package com.myme.mycarforme.domains.car.dto;

import com.myme.mycarforme.domains.car.domain.Car;

public record NormalizedScoresDto(
        Car car,
        double mmScore,
        double accidentCount,
        double initialRegistration,
        double mileage,
        double fuelEfficiency
) {
    public double[] toArray() {
        return new double[]{mmScore, accidentCount, initialRegistration, mileage, fuelEfficiency};
    }
}
