package com.myme.mycarforme.domains.car.dto;

import com.myme.mycarforme.domains.car.domain.Car;

public record MmScoreDto( // 찜 쪽 빼기
                          Long carId, String carName, Long year, Long mileage, Long sellingPrice, String mainImage, Double mmScore, String createdAt, String updatedAt ) {
    public static MmScoreDto of (Car car, Boolean isLike, Long likeCount) {
        return new MmScoreDto(
                car.getId(),
                car.getCarName(),
                car.getYear(),
                car.getMileage(),
                car.getSellingPrice(),
                car.getMainImage(),
                car.getMmScore(),
                car.getCreatedAt() != null ? car.getCreatedAt().toString() : null,
                car.getUpdatedAt() != null ? car.getUpdatedAt().toString() : null
        );
    }
}
