package com.myme.mycarforme.domains.car.dto;

import com.myme.mycarforme.domains.car.domain.Car;

public record CarDto( // 찜 빼고
                      Long carId, String modelName, String year, Long mileage, Long sellingPrice, String mainImage, String carNumber, String createdAt, String updatedAt) {
    public static CarDto of(Car car, Boolean isLike, Integer likeCount) {
        return new CarDto(
                car.getId(),
                car.getCarName(),
                car.getYear().toString(),
                car.getMileage(),
                car.getSellingPrice(),
                car.getMainImage(),
                car.getCarNumber(),
                // isLike,
                // likeCount,
                car.getCreatedAt() != null ? car.getCreatedAt().toString() : null,
                car.getUpdatedAt() != null ? car.getUpdatedAt().toString() : null
        );
    }
}

