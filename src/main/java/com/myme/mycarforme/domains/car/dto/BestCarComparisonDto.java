package com.myme.mycarforme.domains.car.dto;

import com.myme.mycarforme.domains.car.domain.Car;
import com.myme.mycarforme.global.util.helper.DateFormatHelper;

public record BestCarComparisonDto(
    Long carId,
    String carName,
    String mainImage,
    Double mmScore,
    Integer accidentCount,
    String initialRegistration,
    Long mileage,
    Double fuelEfficiency,
    Long sellingPrice,
    String carNumber,
    String exteriorColor,
    String fuelType,
    Long seating,
    String createdAt,
    String updatedAt
) {
    public static BestCarComparisonDto from(Car car) {
        return new BestCarComparisonDto(
                car.getId(),
                car.getCarName(),
                car.getMainImage(),
                car.getMmScore(),
                car.getAccidentHistoryList().size(),
                DateFormatHelper.toKoreanDateString(car.getInitialRegistration()),
                car.getMileage(),
                (car.getCityEfficiency() + car.getHighwayEfficiency()) / 2,
                car.getSellingPrice(),
                car.getCarNumber(),
                car.getExteriorColor(),
                car.getFuelType(),
                car.getSeating(),
                car.getCreatedAt().toString(),
                car.getUpdatedAt().toString()
        );
    }
}
