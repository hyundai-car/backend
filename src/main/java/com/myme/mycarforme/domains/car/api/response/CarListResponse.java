package com.myme.mycarforme.domains.car.api.response;

import com.myme.mycarforme.domains.car.domain.Car;
import com.myme.mycarforme.domains.car.dto.CarDto;

import java.util.List;

public record CarListResponse(List<CarDto> contents) {
    public static CarListResponse of(List<CarDto> carDtos) {
        return new CarListResponse(carDtos);
    }
}
