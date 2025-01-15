package com.myme.mycarforme.domains.car.api.response;

import com.myme.mycarforme.domains.car.dto.CarDetailDto;

public record CarDetailResponse(CarDetailDto car) {
    public static CarDetailResponse from(CarDetailDto carDetailDto) {
        return new CarDetailResponse(carDetailDto);
    }
}