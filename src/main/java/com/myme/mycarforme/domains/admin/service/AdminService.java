package com.myme.mycarforme.domains.admin.service;

import com.myme.mycarforme.domains.admin.api.response.NeedDeliveryListResponse;
import com.myme.mycarforme.domains.admin.constant.ActivityType;
import com.myme.mycarforme.domains.admin.dto.ActivityLogDto;
import com.myme.mycarforme.domains.car.domain.Car;
import com.myme.mycarforme.domains.car.domain.Like;
import com.myme.mycarforme.domains.car.domain.Recommend;
import com.myme.mycarforme.domains.car.dto.CarDetailDto;
import com.myme.mycarforme.domains.car.exception.CarNotFoundException;
import com.myme.mycarforme.domains.car.repository.CarRepository;
import com.myme.mycarforme.domains.car.repository.LikeRepository;
import com.myme.mycarforme.domains.car.repository.RecommendRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {
    private final CarRepository carRepository;
    private final LikeRepository likeRepository;
    private final RecommendRepository recommendRepository;

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

    public Page<ActivityLogDto> getRecentActivities(Pageable pageable) {
        // Like 엔티티 조회 및 변환
        List<ActivityLogDto> likeLogs = likeRepository.findTop100ByOrderByUpdatedAtDesc()
                .stream()
                .map(like -> ActivityLogDto.builder()
                        .activityDate(like.getUpdatedAt())
                        .username(like.getUserId())
                        .activityType(like.getIsLike() ? ActivityType.LIKE_TRUE : ActivityType.LIKE_FALSE)
                        .carId(like.getCar().getId())
                        .carName(like.getCar().getCarName())
                        .build())
                .toList();

        // Recommend 엔티티 조회 및 변환
        List<ActivityLogDto> recommendLogs = recommendRepository.findTop100ByOrderByUpdatedAtDesc()
                .stream()
                .map(recommend -> ActivityLogDto.builder()
                        .activityDate(recommend.getUpdatedAt())
                        .username(recommend.getUserId())
                        .activityType(ActivityType.RECOMMEND)
                        .carId(recommend.getCar().getId())
                        .carName(recommend.getCar().getCarName())
                        .build())
                .toList();

        // 두 리스트 합치기, 정렬, 페이징 처리
        List<ActivityLogDto> allActivities = new ArrayList<>();
        allActivities.addAll(likeLogs);
        allActivities.addAll(recommendLogs);

        List<ActivityLogDto> sortedActivities = allActivities.stream()
                .sorted(Comparator.comparing(ActivityLogDto::activityDate).reversed())
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), sortedActivities.size());

        return new PageImpl<>(
                sortedActivities.subList(start, end),
                pageable,
                sortedActivities.size()
        );
    }

    public Page<Car> getCars(String search, String carType, String fuelType, Integer isOnSale, Pageable pageable) {
        return carRepository.findBySearchCriteria(
                search,
                carType,
                fuelType,
                isOnSale,
                pageable
        );
    }

    public Car getCarDetail(Long id) {
        return carRepository.findById(id)
                .orElseThrow(CarNotFoundException::new);
    }

    @Transactional
    public void deleteCar(Long id) {
        carRepository.deleteById(id);
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

}
