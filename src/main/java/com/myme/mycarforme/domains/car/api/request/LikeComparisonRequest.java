package com.myme.mycarforme.domains.car.api.request;

import java.util.Set;

public record LikeComparisonRequest(
        Set<Long> carIdList
) {
}
