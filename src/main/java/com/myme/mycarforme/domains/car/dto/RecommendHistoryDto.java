package com.myme.mycarforme.domains.car.dto;

import com.myme.mycarforme.domains.car.api.response.RecommendResponse.CarInfo;
import com.myme.mycarforme.domains.car.domain.Car;
import com.myme.mycarforme.domains.car.domain.Recommend;
import com.myme.mycarforme.global.util.helper.DateFormatHelper;
import java.time.LocalDateTime;

public record RecommendHistoryDto(
        Long recommendId,
        CarDto car,
        String recommendedAt,
        Long recommendPriority,
        String recommendCondition,
        String recommendReason,
        String createdAt,
        String updatedAt
) {
    public static RecommendHistoryDto of(Recommend recommend, Boolean isLike, Long likeCount) {
        return new RecommendHistoryDto(
                recommend.getId(),
                CarDto.of(recommend.getCar(), isLike, likeCount),
                DateFormatHelper.toKoreanDateString(recommend.getRecommendedAt().toString()),
                recommend.getRecommendPriority(),
                recommend.getRecommendCondition(),
                recommend.getRecommendReason(),
                recommend.getCreatedAt() != null ? recommend.getCreatedAt().toString() : null,
                recommend.getUpdatedAt() != null ? recommend.getUpdatedAt().toString() : null
        );
    }


}
