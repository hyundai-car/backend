package com.myme.mycarforme.domains.order.api.response;

import com.myme.mycarforme.domains.car.domain.Car;
import com.myme.mycarforme.domains.order.dto.OrderedCarDto;
import java.util.List;

public record OrderedCarListResponse(
        Long contractingId,
        List<OrderedCarDto> orderedCars
) {
    public static OrderedCarListResponse from(Long contractingId, List<Car> orderedCarList) {
        return new OrderedCarListResponse(
                contractingId,
                orderedCarList.stream()
                        .map(OrderedCarDto::from)
                        .toList()
        );
    }
}
