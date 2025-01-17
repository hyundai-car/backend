package com.myme.mycarforme.domains.car.api.response;

import com.myme.mycarforme.global.util.helper.DateFormatHelper;
import java.time.LocalDateTime;
import java.util.List;

public record RecommendResponse(List<RecommendCarInfo> contents) {
    public static RecommendResponse of(List<RecommendCarInfo> contents) {
        return new RecommendResponse(contents);
    }

    public record RecommendCarInfo(
            Long recommendId,
            String recommendedAt,
            Long recommendPriority,
            String recommendCondition,
            String recommendReason,
            String createdAt,
            String updatedAt,
            CarInfo car
    ) {
        public static RecommendCarInfo of(
                Long recommendId,
                String recommendedAt,
                Long recommendPriority,
                String recommendCondition,
                String recommendReason,
                String createdAt,
                String updatedAt,
                CarInfo car) {
            return new RecommendCarInfo(
                    recommendId, recommendedAt, recommendPriority,
                    recommendCondition, recommendReason,
                    createdAt, updatedAt, car);
        }
    }

    public record CarInfo(
            Long carId,
            String carName,
            String initialRegistration,
            Long mileage,
            Long sellingPrice,
            Long likeCount,
            String fuelType,
            String mainImage
    ) {
        public static CarInfo of(
                Long carId,
                String carName,
                String initialRegistration,
                Long mileage,
                Long sellingPrice,
                Long likeCount,
                String fuelType,
                String mainImage) {
            return new CarInfo(
                    carId, carName,  DateFormatHelper.toKoreanDateString(initialRegistration), mileage,
                    sellingPrice, likeCount, fuelType, mainImage);
        }
    }

}
