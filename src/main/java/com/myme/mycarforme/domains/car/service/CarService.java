package com.myme.mycarforme.domains.car.service;

import com.myme.mycarforme.domains.car.api.request.CarSearchRequest;
import com.myme.mycarforme.domains.car.api.response.MmScoreResponse;
import com.myme.mycarforme.domains.car.domain.Car;
import com.myme.mycarforme.domains.car.domain.DetailImage;
import com.myme.mycarforme.domains.car.domain.Exterior360Image;
import com.myme.mycarforme.domains.car.dto.*;
import com.myme.mycarforme.domains.car.exception.CarNotFoundException;
import com.myme.mycarforme.domains.car.exception.ImageNotFoundException;
import com.myme.mycarforme.domains.car.repository.CarRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor

public class CarService {
    private final CarRepository carRepository;

    public List<CarDto> searchCars(CarSearchRequest request) {
        List<Car> cars = carRepository.findAllBySearchCondition(
                request.keyword(),
                request.carType(),
                request.fuelType(),
                request.minSellingPrice(),
                request.maxSellingPrice(),
                request.minMileage(),
                request.maxMileage(),
                request.minYear(),
                request.maxYear()
        );

        return cars.stream()
                .map(car -> CarDto.of(car, false, 0L)) // 현재는 찜 기능이 구현되지 않아 false, 0으로 처리
                .collect(Collectors.toList());
    }

    @Transactional
    public CarDetailDto getCarDetail(Long carId) {
        Car car = carRepository.findByIdWithDetails(carId)
                .orElseThrow(() -> new CarNotFoundException());

        log.info("Found car with id: {}", carId);

        return CarDetailDto.from(car);
    }

    // CarService.java
    @Transactional
    public List<Exterior360ImageDto> get360Images(Long carId) {
        List<Exterior360Image> images = carRepository.findAllByCarIdOrderByRotationDegree(carId);

        if (images.isEmpty()) {
            throw new ImageNotFoundException();
        }

        return images.stream()
                .map(Exterior360ImageDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<DetailImageDto> getDetailImages(Long carId) {
        List<DetailImage> images = carRepository.findAllDetailImagesByCarId(carId);

        if (images.isEmpty()) {
            throw new ImageNotFoundException();
        }

        return images.stream()
                .map(DetailImageDto::from)
                .collect(Collectors.toList());
    }

    public MmScoreResponse getTop5CarsByMmScore() {
        List<Car> top5Cars = carRepository.findTop5ByOrderByMmScoreDesc();
        List<MmScoreDto> mmScoreDtos = top5Cars.stream()
                .map(car -> MmScoreDto.of( car, false, 0L))
                .toList();

        return MmScoreResponse.from(mmScoreDtos);
    }



}
