package com.myme.mycarforme.domains.car.dto;

import com.myme.mycarforme.domains.car.domain.Car;
import com.myme.mycarforme.global.util.helper.DateFormatHelper;

public record LikeCarDto(
        Long carId,
        String carName,
        String initialRegistration,
        Long mileage,
        Long sellingPrice,
        String mainImage
) {
    public static LikeCarDto from(Car car) {
        return new LikeCarDto(
                car.getId(),
                car.getCarName(),
                DateFormatHelper.toKoreanDateString(car.getInitialRegistration()),
                car.getMileage(),
                car.getSellingPrice(),
                car.getMainImage()
        );
    }
}
