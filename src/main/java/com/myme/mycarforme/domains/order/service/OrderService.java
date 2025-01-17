package com.myme.mycarforme.domains.order.service;

import com.myme.mycarforme.domains.car.domain.Car;
import com.myme.mycarforme.domains.car.exception.CarNotFoundException;
import com.myme.mycarforme.domains.car.repository.CarRepository;
import com.myme.mycarforme.domains.order.api.response.ContractResponse;
import com.myme.mycarforme.domains.order.api.response.OrderStatusResponse;
import com.myme.mycarforme.domains.order.api.response.OrderedCarListResponse;
import com.myme.mycarforme.domains.order.constant.OrderStatus;
import com.myme.mycarforme.domains.order.exception.DuplicatedOrderException;
import com.myme.mycarforme.domains.order.exception.OrderNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final CarRepository carRepository;

    public OrderedCarListResponse getOrderedCarList(String userId) {
        List<Car> orderedCarList = carRepository.findByBuyerId(userId);
        List<Car> contractingList = carRepository.findByBuyerIdAndIsOnSaleNot(userId, 2);

        return contractingList.isEmpty()
                ? OrderedCarListResponse.from(null, orderedCarList)
                : OrderedCarListResponse.from(contractingList.get(0).getId(), orderedCarList);
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

    @Transactional
    public ContractResponse updateContractStatus(String userId, String userName, String email, String phoneNumber, Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(CarNotFoundException::new);

        // 해당 차가 계약 중이라면 에러
        if(car.getBuyerId() != null || car.getPaymentDeliveryStatus() != 0) {
            throw new DuplicatedOrderException();
        }

        // 해당 유저가 계약 중이라면 에러
        List<Car> orderedCarList = carRepository.findByBuyerIdAndIsOnSaleNot(userId, 2);
        if(!orderedCarList.isEmpty()) {
            throw new DuplicatedOrderException();
        }

        // 계약 진행
        car.doContract(userId);
        Car updatedCar = carRepository.save(car);

        // 문자 발송

        return ContractResponse.of(
                userName,
                email,
                phoneNumber,
                updatedCar,
                OrderStatus.CONTRACTED.getStatus(),
                300000L
        );
    }

    @Transactional
    public ContractResponse updatePaymentStatus(String userId, String userName, String email, String phoneNumber, Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(CarNotFoundException::new);

        // 해당 차가 계약 중이라면 에러
        if(car.getBuyerId() == null
                || !car.getBuyerId().equals(userId)
                || car.getPaymentDeliveryStatus() != 1) {
            throw new DuplicatedOrderException();
        }

        // 해당 유저가 계약 중이라면 에러
        List<Car> orderedCarList = carRepository.findByBuyerIdAndIsOnSaleNot(userId, 2);
        if(orderedCarList.size() != 1 || orderedCarList.get(0).getPaymentDeliveryStatus() != 1) {
            throw new DuplicatedOrderException();
        }

        // 결제 진행
        car.doPay();
        Car updatedCar = carRepository.save(car);

        return ContractResponse.of(
                userName,
                email,
                phoneNumber,
                updatedCar,
                OrderStatus.PAID.getStatus(),
                updatedCar.getSellingPrice() * 10000 - 300000L
        );
    }
}
