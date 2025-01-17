package com.myme.mycarforme.domains.car.dto;

import com.myme.mycarforme.domains.car.domain.Car;
import com.myme.mycarforme.global.util.helper.DateFormatHelper;

public record MmScoreDto( // 찜 쪽 빼기
                          Long carId, String carName, String initialRegistration, Long mileage, Long sellingPrice, String mainImage, Double mmScore, Boolean isLike, Long likeCount, String createdAt, String updatedAt ) {
    public static MmScoreDto of (Car car, Boolean isLike, Long likeCount) {
        return new MmScoreDto(
                car.getId(),
                car.getCarName(),
                DateFormatHelper.toKoreanDateString(car.getInitialRegistration()),
                car.getMileage(),
                car.getSellingPrice(),
                car.getMainImage(),
                car.getMmScore(),
                isLike,
                likeCount,
                car.getCreatedAt() != null ? car.getCreatedAt().toString() : null,
                car.getUpdatedAt() != null ? car.getUpdatedAt().toString() : null
        );
    }
}
