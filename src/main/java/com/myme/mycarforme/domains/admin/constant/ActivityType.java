package com.myme.mycarforme.domains.admin.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ActivityType {
    LIKE_TRUE("찜 등록"),
    LIKE_FALSE("찜 해제"),
    RECOMMEND("차량 추천");

    private final String status;
}
