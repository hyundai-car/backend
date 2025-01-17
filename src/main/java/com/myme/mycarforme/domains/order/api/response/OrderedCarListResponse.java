package com.myme.mycarforme.domains.order.api.response;

import com.myme.mycarforme.domains.car.domain.Car;
import java.util.List;

public record OrderedCarListResponse(
        List<Long> orderedCarIds
) {
    public static OrderedCarListResponse from(List<Car> orderedCarList) {
        return new OrderedCarListResponse(
                orderedCarList.stream()
                        .map(Car::getId)
                        .toList()
        );
    }
}
