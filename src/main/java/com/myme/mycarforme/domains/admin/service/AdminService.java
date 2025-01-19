package com.myme.mycarforme.domains.admin.service;

import com.myme.mycarforme.domains.admin.api.response.NeedDeliveryListResponse;
import com.myme.mycarforme.domains.admin.dto.ActivityLogDto;
import com.myme.mycarforme.domains.car.domain.Car;
import com.myme.mycarforme.domains.car.exception.CarNotFoundException;
import com.myme.mycarforme.domains.car.repository.CarRepository;
import com.myme.mycarforme.domains.car.repository.LikeRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {
    private final CarRepository carRepository;
    private final LikeRepository likeRepository;

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

    public long getTotalCarCount() {
        return carRepository.count();
    }

    public long getTodayOrderCount() {
        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.now().with(LocalTime.MAX);
        return carRepository.countByCreatedAtBetweenAndStatusNotZero(startOfDay, endOfDay);
    }

    public List<Car> getAllOrders() {
        return carRepository.findAllOrdersByUpdatedAtDesc();
    }

    // 상태별 주문 조회
    public List<Car> getOrdersByStatus(Integer status) {
        return carRepository.findAllOrdersByStatusOrderByUpdatedAtDesc(status);
    }

    // 주문 상태 업데이트
    @Transactional
    public void updateOrderStatus(Long carId, Integer status) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("해당 차량이 존재하지 않습니다."));

        switch (status) {
            case 1 -> car.doContract(car.getBuyerId());
            case 2 -> car.doPay();
            case 3 -> car.doDeliveryStarted();
            case 4 -> car.doDeliveryEnded();
        }
    }

    // 주문 상태 초기화
    @Transactional
    public void resetOrderStatus(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("해당 차량이 존재하지 않습니다."));
        car.resetOrderStatus();
    }



}
