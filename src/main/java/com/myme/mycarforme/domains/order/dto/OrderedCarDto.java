package com.myme.mycarforme.domains.order.dto;

import com.myme.mycarforme.domains.car.domain.Car;
import com.myme.mycarforme.global.util.helper.DateFormatHelper;

public record OrderedCarDto(
        Long carId,
        String carName,
        String carNumber,
        String initialRegistration,
        Long mileage,
        Long sellingPrice,
        String mainImage,
        Integer likecount
) {
    public static OrderedCarDto from(Car car) {
        return new OrderedCarDto(
                car.getId(),
                car.getCarName(),
                car.getCarNumber(),
                DateFormatHelper.toKoreanDateString(car.getInitialRegistration()),
                car.getMileage(),
                car.getSellingPrice(),
                car.getMainImage(),
                car.getLikeList().size()
        );
    }
}
