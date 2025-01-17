package com.myme.mycarforme.domains.car.service;

import static com.myme.mycarforme.global.util.helper.CalculateHelper.calculateAverageDate;

import com.myme.mycarforme.domains.car.api.response.LikeCarListResponse;
import com.myme.mycarforme.domains.car.api.response.LikeComparisonResponse;
import com.myme.mycarforme.domains.car.api.response.LikeResponse;
import com.myme.mycarforme.domains.car.domain.Car;
import com.myme.mycarforme.domains.car.domain.Like;
import com.myme.mycarforme.domains.car.dto.NormalizedScoresDto;
import com.myme.mycarforme.domains.car.exception.CarNotFoundException;
import com.myme.mycarforme.domains.car.exception.LikeComparisonError;
import com.myme.mycarforme.domains.car.repository.CarRepository;
import com.myme.mycarforme.domains.car.repository.LikeRepository;
import com.myme.mycarforme.global.util.helper.CalculateHelper;
import com.myme.mycarforme.global.util.helper.CalculateHelper.CarScoreNormalizer;
import jakarta.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
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

    public LikeComparisonResponse getComparisonLikeCarList(String userId, Set<Long> carIdList) {
        // 1. 길이 체크
        if (carIdList.size() < 2) {
            throw LikeComparisonError.invalidLength();
        }

        // 2. 차량과 찜 상태 조회
        List<Car> cars = carRepository.findAllById(carIdList);
        List<Like> likes = likeRepository.findByUserIdAndCarIdIn(userId, carIdList);

        if (cars.size() != carIdList.size()) {
            throw LikeComparisonError.carNotFound();
        }
        if (likes.size() != carIdList.size()) {
            throw LikeComparisonError.likeNotFound();
        }

        // 3. 정규화된 점수 계산
        List<NormalizedScoresDto> normalizedScores = cars.stream()
                .map(car -> new NormalizedScoresDto(
                        car,
                        car.getMmScore(),
                        CarScoreNormalizer.normalizeAccidentCount(car.getAccidentHistoryList().size()),
                        CarScoreNormalizer.normalizeInitialRegistration(car.getInitialRegistration()),
                        CarScoreNormalizer.normalizeMileage(car.getMileage()),
                        CarScoreNormalizer.normalizeFuelEfficiency((car.getCityEfficiency() + car.getHighwayEfficiency()) / 2)
                ))
                .toList();

        // 4. 최고 차량 찾기
        NormalizedScoresDto bestCar = normalizedScores.stream()
                .peek(score -> {
                    double[] values = score.toArray();
                    double area = CalculateHelper.calculatePentagonArea(values);
                    log.debug("""
           Car ID: {}
           Values: [{}, {}, {}, {}, {}]
           Area: {}
           ---------------
           """,
                            score.car().getId(),
                            values[0], values[1], values[2], values[3], values[4],
                            area);
                })
                .max(Comparator.comparingDouble(score ->
                        CalculateHelper.calculatePentagonArea(score.toArray())))
                .orElseThrow();

        // 5. 원본 값들의 평균 계산
        double mmScoreAvg = cars.stream().mapToDouble(Car::getMmScore).average().orElseThrow();
        double accidentCountAvg = cars.stream().mapToDouble(car -> car.getAccidentHistoryList().size()).average().orElseThrow();
        double mileageAvg = cars.stream().mapToDouble(Car::getMileage).average().orElseThrow();
        double fuelEfficiencyAvg = cars.stream().mapToDouble(car -> (car.getCityEfficiency() + car.getHighwayEfficiency()) / 2).average().orElseThrow();
        String initialRegistrationAvg = calculateAverageDate(cars);

        // 6. 정규화된 값들의 평균 계산
        double avgMmScoreNorm = normalizedScores.stream().mapToDouble(NormalizedScoresDto::mmScore).average().orElseThrow();
        double avgAccidentCountNorm = normalizedScores.stream().mapToDouble(NormalizedScoresDto::accidentCount).average().orElseThrow();
        double avgInitialRegistrationNorm = normalizedScores.stream().mapToDouble(NormalizedScoresDto::initialRegistration).average().orElseThrow();
        double avgMileageNorm = normalizedScores.stream().mapToDouble(NormalizedScoresDto::mileage).average().orElseThrow();
        double avgFuelEfficiencyNorm = normalizedScores.stream().mapToDouble(NormalizedScoresDto::fuelEfficiency).average().orElseThrow();

        // 7. 다른 차량 ID 리스트
        List<Long> otherCarIds = cars.stream()
                .map(Car::getId)
                .filter(id -> !id.equals(bestCar.car().getId()))
                .toList();

        return LikeComparisonResponse.of(
                bestCar.car(),
                mmScoreAvg,
                accidentCountAvg,
                initialRegistrationAvg,
                mileageAvg,
                fuelEfficiencyAvg,
                bestCar.mmScore(),
                bestCar.accidentCount(),
                bestCar.initialRegistration(),
                bestCar.mileage(),
                bestCar.fuelEfficiency(),
                avgMmScoreNorm,
                avgAccidentCountNorm,
                avgInitialRegistrationNorm,
                avgMileageNorm,
                avgFuelEfficiencyNorm,
                otherCarIds
        );
    }
}
