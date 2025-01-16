package com.myme.mycarforme.domains.car.dto;

import com.myme.mycarforme.domains.car.domain.Car;
import com.myme.mycarforme.global.util.helper.DateFormatHelper;

public record CarDto( // 찜 빼고
                      Long carId, String carName, String initialRegistration, Long mileage, Long sellingPrice, String mainImage, String carNumber, Boolean isLike, Long likeCount, String createdAt, String updatedAt) {
    public static CarDto of(Car car, Boolean isLike, Long likeCount) {
        return new CarDto(
                car.getId(),
                car.getCarName(),
                DateFormatHelper.toKoreanDateString(car.getInitialRegistration()),
                car.getMileage(),
                car.getSellingPrice(),
                car.getMainImage(),
                car.getCarNumber(),
                isLike,
                likeCount,
                car.getCreatedAt() != null ? car.getCreatedAt().toString() : null,
                car.getUpdatedAt() != null ? car.getUpdatedAt().toString() : null
        );
    }
}

