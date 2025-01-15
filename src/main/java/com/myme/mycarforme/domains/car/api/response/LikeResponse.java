package com.myme.mycarforme.domains.car.api.response;

public record LikeResponse(
        Long carId,
        Boolean isLike
) {
}
