package com.myme.mycarforme.domains.order.service;

import com.myme.mycarforme.domains.car.domain.Car;
import com.myme.mycarforme.domains.car.exception.CarNotFoundException;
import com.myme.mycarforme.domains.car.repository.CarRepository;
import com.myme.mycarforme.domains.fcm.service.FCMTokenService;
import com.myme.mycarforme.domains.order.api.response.ContractResponse;
import com.myme.mycarforme.domains.order.api.response.OrderStatusResponse;
import com.myme.mycarforme.domains.order.api.response.OrderedCarListResponse;
import com.myme.mycarforme.domains.order.api.response.TrackingCodeResponse;
import com.myme.mycarforme.domains.order.constant.OrderStatus;
import com.myme.mycarforme.domains.order.exception.DuplicatedOrderException;
import com.myme.mycarforme.domains.order.exception.InvalidOrderStatusException;
import com.myme.mycarforme.domains.order.exception.OrderNotFoundException;
import com.myme.mycarforme.domains.order.exception.UserTrackingCodeNotFoundException;
import com.myme.mycarforme.global.config.websocket.ActiveTrackingManager;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final CarRepository carRepository;
    private final ActiveTrackingManager activeTrackingManager;
    private final FCMTokenService fcmTokenService;

    public OrderedCarListResponse getOrderedCarList(String userId) {
        List<Car> orderedCarList = carRepository.findByBuyerId(userId);
        List<Car> contractingList = carRepository.findByBuyerIdAndIsOnSale(userId, 1);

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

        // 해당 차량의 주문 상태 체크
        if (car.getBuyerId() != null) {
            // 1. 내가 주문한 차량인 경우
            if (car.getBuyerId().equals(userId)) {
                if (car.getPaymentDeliveryStatus() != 0) {
                    throw new InvalidOrderStatusException();
                }
            }
            // 2. 다른 사람이 주문한 차량인 경우
            else {
                throw DuplicatedOrderException.forCar();
            }
        }

        // 해당 유저의 다른 주문 존재 여부 체크
        List<Car> orderedCarList = carRepository.findByBuyerIdAndIsOnSale(userId, 1);
        if (!orderedCarList.isEmpty()) {
            Car orderedCar = orderedCarList.get(0);
            if (!orderedCar.getId().equals(carId)) {
                throw DuplicatedOrderException.byUser();
            }
        }

        // 계약 진행
        car.doContract(userId);
        Car updatedCar = carRepository.save(car);

        // TODO : 문자 발송

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

        // 해당 차량의 주문 상태 체크
        if (car.getBuyerId() != null) {
            // 1. 내가 주문한 차량인 경우
            if (car.getBuyerId().equals(userId)) {
                if (car.getPaymentDeliveryStatus() != 1) {
                    throw new InvalidOrderStatusException();
                }
            }
            // 2. 다른 사람이 주문한 차량인 경우
            else {
                throw DuplicatedOrderException.forCar();
            }
        }

        // 주문 상태가 다른 경우
        if (!car.getPaymentDeliveryStatus().equals(1)) {
            throw new InvalidOrderStatusException();
        }

        // 해당 유저의 다른 주문 존재 여부 체크
        List<Car> orderedCarList = carRepository.findByBuyerIdAndIsOnSale(userId, 1);
        if (!orderedCarList.isEmpty()) {
            Car orderedCar = orderedCarList.get(0);
            if (!orderedCar.getId().equals(carId)) {
                throw DuplicatedOrderException.byUser();
            }
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

    @Transactional
    public void updateDeliveryStartedStatus(String userId) {
        List<Car> carList = carRepository.findByBuyerIdAndIsOnSale(userId, 2);

        if(carList.size() != 1) {
            throw DuplicatedOrderException.byUser();
        }

        Car car = carList.get(0);

        // 해당 차량의 주문 상태 체크
        if (car.getBuyerId() != null) {
            // 1. 내가 주문한 차량인 경우
            if (car.getBuyerId().equals(userId)) {
                if (car.getPaymentDeliveryStatus() != 2) {
                    throw new InvalidOrderStatusException();
                }
            }
            // 2. 다른 사람이 주문한 차량인 경우
            else {
                throw DuplicatedOrderException.forCar();
            }
        }

        // 주문 상태가 다른 경우
        if (!car.getPaymentDeliveryStatus().equals(2)) {
            throw new InvalidOrderStatusException();
        }

        // 배송 시작
        car.doDeliveryStarted();
        carRepository.save(car);

        //FCM 알림 발송
        fcmTokenService.sendNotification(
                userId,
                "MyCarForMe 차량 탁송 알림",
                "차량 탁송이 시작됐어요!\n(" + car.getCarName() + ")"
        );
    }

    @Transactional
    public void updateDeliveryEndedStatus(String userId) {
        List<Car> carList = carRepository.findByBuyerIdAndIsOnSale(userId, 2);

        if(carList.size() != 1) {
            throw DuplicatedOrderException.byUser();
        }

        Car car = carList.get(0);

        // 해당 차량의 주문 상태 체크
        if (car.getBuyerId() != null) {
            // 1. 내가 주문한 차량인 경우
            if (car.getBuyerId().equals(userId)) {
                if (car.getPaymentDeliveryStatus() != 3) {
                    throw new InvalidOrderStatusException();
                }
            }
            // 2. 다른 사람이 주문한 차량인 경우
            else {
                throw DuplicatedOrderException.forCar();
            }
        }

        // 주문 상태가 다른 경우
        if (!car.getPaymentDeliveryStatus().equals(3)) {
            throw new InvalidOrderStatusException();
        }

        // 배송 시작
        car.doDeliveryEnded();
        carRepository.save(car);

        // FCM 알림 발송
        fcmTokenService.sendNotification(
                userId,
                "MyCarForMe 차량 탁송 알림",
                "차량 탁송이 완료되었어요!\n(" + car.getCarName() + ")"
        );
    }

    public TrackingCodeResponse getTrackingCode(String userId) {
        if(activeTrackingManager.isActive(userId)) {
            return new TrackingCodeResponse(userId);
        } else {
            throw new UserTrackingCodeNotFoundException();
        }
    }
}
