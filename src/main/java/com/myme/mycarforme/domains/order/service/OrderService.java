package com.myme.mycarforme.domains.order.service;

import com.myme.mycarforme.domains.car.domain.Car;
import com.myme.mycarforme.domains.car.exception.CarNotFoundException;
import com.myme.mycarforme.domains.car.repository.CarRepository;
import com.myme.mycarforme.domains.order.api.response.OrderStatusResponse;
import com.myme.mycarforme.domains.order.api.response.OrderedCarListResponse;
import com.myme.mycarforme.domains.order.exception.OrderNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final CarRepository carRepository;

    public OrderedCarListResponse getOrderedCarList(String userId) {
        List<Car> orderedCarList = carRepository.findByBuyerId(userId);

        return OrderedCarListResponse.from(orderedCarList);
    }

    public OrderStatusResponse getOrderStatus(String userId, Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(CarNotFoundException::new);

        if(car.getBuyerId() == null || !car.getBuyerId().equals(userId)) {
            throw new OrderNotFoundException();
        }

        return OrderStatusResponse.of(
                car.getId(),
                car.getPaymentDeliveryStatus(),
                car.getContractedAt(),
                car.getPayedAt(),
                car.getDeliveryStartedAt(),
                car.getDeliveryEndedAt(),
                car.getCreatedAt(),
                car.getUpdatedAt()
        );
    }
}
