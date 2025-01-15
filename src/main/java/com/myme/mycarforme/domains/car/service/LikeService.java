package com.myme.mycarforme.domains.car.service;

import com.myme.mycarforme.domains.car.api.response.LikeResponse;
import com.myme.mycarforme.domains.car.domain.Car;
import com.myme.mycarforme.domains.car.domain.Like;
import com.myme.mycarforme.domains.car.exception.CarNotFoundException;
import com.myme.mycarforme.domains.car.repository.CarRepository;
import com.myme.mycarforme.domains.car.repository.LikeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class LikeService {
    private final CarRepository carRepository;
    private final LikeRepository likeRepository;

    public LikeResponse toggleLike(String userId, Long carId) {
        Car currentCar = carRepository.findById(carId)
                .orElseThrow(CarNotFoundException::new);

        Like currentLike = likeRepository.findByUserIdAndCarId(userId, currentCar.getId())
                .orElseGet(() -> Like.builder()
                        .userId(userId)
                        .car(currentCar)
                        .isLike(false)
                        .build()
                );

        currentLike.toggleLike();

        likeRepository.save(currentLike);

        return new LikeResponse(currentCar.getId(), currentLike.getIsLike());
    }

    public LikeResponse getLikeByCarId(String userId, Long carId) {
        Car currentCar = carRepository.findById(carId)
                .orElseThrow(CarNotFoundException::new);

        Like currentLike = likeRepository.findByUserIdAndCarId(userId, currentCar.getId())
                .orElseGet(() -> Like.builder()
                        .userId(userId)
                        .car(currentCar)
                        .isLike(false)
                        .build()
                );

        return new LikeResponse(currentCar.getId(), currentLike.getIsLike());
    }
}
