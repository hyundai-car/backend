package com.myme.mycarforme.domains.order.api.response;

import com.myme.mycarforme.domains.car.domain.Car;
import java.util.List;

public record OrderedCarListResponse(
        Long contractingId,
        List<Long> orderedCarIds
) {
    public static OrderedCarListResponse from(Long contractingId, List<Car> orderedCarList) {
        return new OrderedCarListResponse(
                contractingId,
                orderedCarList.stream()
                        .map(Car::getId)
                        .toList()
        );
    }
}
