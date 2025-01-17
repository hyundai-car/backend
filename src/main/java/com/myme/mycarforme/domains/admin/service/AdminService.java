package com.myme.mycarforme.domains.admin.service;

import com.myme.mycarforme.domains.admin.api.response.NeedDeliveryListResponse;
import com.myme.mycarforme.domains.car.domain.Car;
import com.myme.mycarforme.domains.car.exception.CarNotFoundException;
import com.myme.mycarforme.domains.car.repository.CarRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {
    private final CarRepository carRepository;

    public void orderStatusReset(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(CarNotFoundException::new);

        car.resetOrderStatus();

        carRepository.save(car);
    }

    public NeedDeliveryListResponse getNeedDelivery() {
        List<String> needDeliveryList = carRepository.findDistinctBuyerIdsByPaymentDeliveryStatus(2);
        return new NeedDeliveryListResponse(needDeliveryList);
    }
}
