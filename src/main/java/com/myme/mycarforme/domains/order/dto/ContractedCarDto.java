package com.myme.mycarforme.domains.order.dto;

import com.myme.mycarforme.domains.car.domain.Car;
import com.myme.mycarforme.global.util.helper.DateFormatHelper;

public record ContractedCarDto(
        Long carId,
        String carName,
        String initialRegistration,
        Long mileage,
        Long price
) {
    public static ContractedCarDto from(Car car) {
        return new ContractedCarDto(
                car.getId(),
                car.getCarName(),
                DateFormatHelper.toKoreanDateString(car.getInitialRegistration()),
                car.getMileage(),
                car.getSellingPrice()
        );
    }
}
