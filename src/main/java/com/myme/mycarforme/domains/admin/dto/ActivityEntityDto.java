package com.myme.mycarforme.domains.admin.dto;

import com.myme.mycarforme.domains.admin.constant.ActivityType;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ActivityEntityDto {
    // Getters
    private final LocalDateTime updatedAt;
    private final String userId;
    private final ActivityType activityType;
    private final Long carId;
    private final String carName;
    private final String additionalInfo;

    public ActivityEntityDto(
            LocalDateTime updatedAt,
            String userId,
            ActivityType activityType,
            Long carId,
            String carName,
            String additionalInfo
    ) {
        this.updatedAt = updatedAt;
        this.userId = userId;
        this.activityType = activityType;
        this.carId = carId;
        this.carName = carName;
        this.additionalInfo = additionalInfo;
    }

}
