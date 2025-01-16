package com.myme.mycarforme.domains.car.service;

import com.myme.mycarforme.domains.car.api.response.LikeCarListResponse;
import com.myme.mycarforme.domains.car.api.response.LikeResponse;
import com.myme.mycarforme.domains.car.domain.Car;
import com.myme.mycarforme.domains.car.domain.Like;
import com.myme.mycarforme.domains.car.exception.CarNotFoundException;
import com.myme.mycarforme.domains.car.repository.CarRepository;
import com.myme.mycarforme.domains.car.repository.LikeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

        Like newLike = likeRepository.save(currentLike);

        return new LikeResponse(
                currentCar.getId(),
                newLike.getIsLike(),
                newLike.getCreatedAt().toString(),
                newLike.getUpdatedAt().toString());
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

        if(currentLike.getCreatedAt() == null) {
            return new LikeResponse(
                    currentCar.getId(),
                    currentLike.getIsLike(),
                    "null",
                    "null");
        } else {
            return new LikeResponse(
                    currentCar.getId(),
                    currentLike.getIsLike(),
                    currentLike.getCreatedAt().toString(),
                    currentLike.getUpdatedAt().toString());
        }
    }

    public LikeCarListResponse getLikeCarList(String userId, Pageable pageable) {
        Page<Like> likeCarList = likeRepository.findCarsByUserIdAndIsLikeTrue(userId, pageable);

        return LikeCarListResponse.from(likeCarList);
    }
}
