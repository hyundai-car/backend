package com.myme.mycarforme.domains.admin.dto;

import com.myme.mycarforme.domains.admin.constant.ActivityType;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ActivityLogDto(
        LocalDateTime activityDate,
        String username,
        ActivityType activityType,
        String detail
) {
}
