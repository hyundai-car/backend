package com.myme.mycarforme.domains.car.service;

import com.myme.mycarforme.domains.car.api.response.LikeCarListResponse;
import com.myme.mycarforme.domains.car.api.response.LikeComparisonResponse;
import com.myme.mycarforme.domains.car.api.response.LikeResponse;
import com.myme.mycarforme.domains.car.domain.Car;
import com.myme.mycarforme.domains.car.domain.Like;
import com.myme.mycarforme.domains.car.domain.OptionList;
import com.myme.mycarforme.domains.car.exception.CarNotFoundException;
import com.myme.mycarforme.domains.car.repository.CarRepository;
import com.myme.mycarforme.domains.car.repository.LikeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {
    @Mock
    private CarRepository carRepository;

    @Mock
    private LikeRepository likeRepository;

    @InjectMocks
    private LikeService likeService;

    private final String TEST_USER_ID = "test-user-id";
    private final Long TEST_CAR_ID = 1L;

    private Car TEST_CAR;

    private Like TEST_LIKE_TRUE;
    private Like TEST_LIKE_FALSE;

    @BeforeEach
    void setUp() {
        TEST_CAR = Car.builder()
                .id(TEST_CAR_ID) // ID를 포함
                .carName("Test car")
                .carType("luxury")
                .year(2022L)
                .initialRegistration("2022.04.11")
                .mileage(31200L)
                .driveType("Auto")
                .displacement(1999L)
                .sellingPrice(4800L)
                .exteriorColor("color")
                .interiorColor("color")
                .seating(5L)
                .fuelType("gasoline")
                .transmissionType("2WD")
                .isOnSale(1)
                .location("location")
                .mmScore(90.11)
                .fuelEfficiency(10.4)
                .mainImage("https://image")
                .newCarPrice(6000L)
                .savingAccount(1200.0)
                .carNumber("123가1234")
                .accidentSeverity(0.0)
                .repairProbability(30.4)
                .predictedPrice(5000.0)
                .cityEfficiency(9.8)
                .highwayEfficiency(11.0)
                .paymentDeliveryStatus(0)
                .optionList(OptionList.builder().build())
                .build();

        TEST_LIKE_TRUE = Like.builder()
                .car(TEST_CAR)
                .userId(TEST_USER_ID)
                .isLike(true)
                .build();

        TEST_LIKE_FALSE = Like.builder()
                .car(TEST_CAR)
                .userId(TEST_USER_ID)
                .isLike(false)
                .build();

        LocalDateTime now = LocalDateTime.now();
        ReflectionTestUtils.setField(TEST_LIKE_TRUE, "createdAt", now);
        ReflectionTestUtils.setField(TEST_LIKE_TRUE, "updatedAt", now);

        ReflectionTestUtils.setField(TEST_LIKE_FALSE, "createdAt", now);
        ReflectionTestUtils.setField(TEST_LIKE_FALSE, "updatedAt", now);
    }

    @Test
    void toggleLike_whenValidCar_thenReturnTrue() {
        // Given
        when(carRepository.findById(TEST_CAR_ID)).thenReturn(Optional.of(TEST_CAR));
        when(likeRepository.findByUserIdAndCarId(TEST_USER_ID, TEST_CAR_ID)).thenReturn(Optional.empty());
        when(likeRepository.save(any(Like.class))).thenReturn(TEST_LIKE_TRUE);

        // When
        LikeResponse result = likeService.toggleLike(TEST_USER_ID, TEST_CAR_ID);

        // Then
        ArgumentCaptor<Like> captor = ArgumentCaptor.forClass(Like.class);
        verify(likeRepository).save(captor.capture());
        Like capturedLike = captor.getValue();

        assertThat(capturedLike.getUserId()).isEqualTo(TEST_USER_ID);
        assertThat(capturedLike.getCar().getId()).isEqualTo(TEST_CAR_ID);
        assertThat(capturedLike.getIsLike()).isTrue();
        assertThat(result.carId()).isEqualTo(TEST_CAR_ID);
        assertThat(result.isLike()).isTrue();
    }

    @Test
    void toggleLike_whenInvalidCar_throwException() {
        // Given
        when(carRepository.findById(TEST_CAR_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> likeService.toggleLike(TEST_USER_ID, TEST_CAR_ID))
                .isInstanceOf(CarNotFoundException.class);

        verify(carRepository).findById(TEST_CAR_ID);
        verify(likeRepository, never()).findByUserIdAndCarId(anyString(), anyLong());
        verify(likeRepository, never()).save(any());
    }

    @Test
    void toggleLike_whenCarExistAndIsLikeTrue_thenReturnFalse() {
        // Given
        when(carRepository.findById(TEST_CAR_ID)).thenReturn(Optional.of(TEST_CAR));
        when(likeRepository.findByUserIdAndCarId(TEST_USER_ID, TEST_CAR_ID)).thenReturn(Optional.of(TEST_LIKE_TRUE));
        when(likeRepository.save(any(Like.class))).thenReturn(TEST_LIKE_FALSE);

        // When
        LikeResponse result = likeService.toggleLike(TEST_USER_ID, TEST_CAR_ID);

        // Then
        verify(likeRepository).save(any());
        assertThat(result.carId()).isEqualTo(TEST_CAR_ID);
        assertThat(result.isLike()).isFalse();
    }

    @Test
    void toggleLike_whenCarExistAndIsLikeFalse_thenReturnTrue() {
        // Given
        when(carRepository.findById(TEST_CAR_ID)).thenReturn(Optional.of(TEST_CAR));
        when(likeRepository.findByUserIdAndCarId(TEST_USER_ID, TEST_CAR_ID)).thenReturn(Optional.of(TEST_LIKE_FALSE));
        when(likeRepository.save(any(Like.class))).thenReturn(TEST_LIKE_TRUE);

        // When
        LikeResponse result = likeService.toggleLike(TEST_USER_ID, TEST_CAR_ID);

        // Then
        verify(likeRepository).save(any());
        assertThat(result.carId()).isEqualTo(TEST_CAR_ID);
        assertThat(result.isLike()).isTrue();
    }

    @Test
    void getLikeByCarId_whenCarExistAndLikeIsTrue_thenReturnLike() {
        // Given
        when(carRepository.findById(TEST_CAR_ID)).thenReturn(Optional.of(TEST_CAR));
        when(likeRepository.findByUserIdAndCarId(TEST_USER_ID, TEST_CAR_ID)).thenReturn(Optional.of(TEST_LIKE_TRUE));

        // When
        LikeResponse result = likeService.getLikeByCarId(TEST_USER_ID, TEST_CAR_ID);

        // Then
        assertThat(result.carId()).isEqualTo(TEST_CAR_ID);
        assertThat(result.isLike()).isTrue();
    }

    @Test
    void getLikeByCarId_whenCarExistAndLikeIsFalse_thenReturnLike() {
        // Given
        when(carRepository.findById(TEST_CAR_ID)).thenReturn(Optional.of(TEST_CAR));
        when(likeRepository.findByUserIdAndCarId(TEST_USER_ID, TEST_CAR_ID)).thenReturn(Optional.of(TEST_LIKE_FALSE));

        // When
        LikeResponse result = likeService.getLikeByCarId(TEST_USER_ID, TEST_CAR_ID);

        // Then
        assertThat(result.carId()).isEqualTo(TEST_CAR_ID);
        assertThat(result.isLike()).isFalse();
    }

    @Test
    void getLikeByCarId_whenCarExistAndLikeNull_thenReturnLike() {
        // Given
        when(carRepository.findById(TEST_CAR_ID)).thenReturn(Optional.of(TEST_CAR));
        when(likeRepository.findByUserIdAndCarId(TEST_USER_ID, TEST_CAR_ID))
                .thenReturn(Optional.empty());

        // When
        LikeResponse result = likeService.getLikeByCarId(TEST_USER_ID, TEST_CAR_ID);

        // Then
        assertThat(result.carId()).isEqualTo(TEST_CAR_ID);
        assertThat(result.isLike()).isFalse();
    }

    @Test
    void getLikeByCarId_whenCarNull_throwException() {
        // Given
        when(carRepository.findById(TEST_CAR_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> likeService.getLikeByCarId(TEST_USER_ID, TEST_CAR_ID))
                .isInstanceOf(CarNotFoundException.class);

        verify(carRepository).findById(TEST_CAR_ID);
    }

    @Test
    void getLikeCarList_whenValidData_thenReturnLikeList() {
        // Given
        Car car = Car.builder()
                .id(1L)
                .carName("Test Car")
                .mileage(1000L)
                .sellingPrice(2000L)
                .initialRegistration("2020.12.01")
                .build();

        Like like = Like.builder()
                .id(1L)
                .userId(TEST_USER_ID)
                .car(car)
                .isLike(true)
                .build();

        LocalDateTime now = LocalDateTime.now();
        ReflectionTestUtils.setField(like, "createdAt", now);
        ReflectionTestUtils.setField(like, "updatedAt", now);

        List<Like> likeCarList = List.of(like);
        PageImpl<Like> carPage = new PageImpl<>(likeCarList);

        when(likeRepository.findCarsByUserIdAndIsLikeTrue(eq(TEST_USER_ID), any(Pageable.class)))
                .thenReturn(carPage);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        LikeCarListResponse result = likeService.getLikeCarList(TEST_USER_ID, pageable);

        // Then
        assertThat(result.contents()).hasSize(1);
        assertThat(result.contents().get(0))
                .extracting("carId", "carName", "mileage", "sellingPrice")
                .containsExactly(1L, "Test Car", 1000L, 2000L);

        verify(likeRepository).findCarsByUserIdAndIsLikeTrue(eq(TEST_USER_ID), any(Pageable.class));
    }

    @Test
    void getComparisonLikeCarList_whenValidData_thenReturnComparisonLikeList() {
        // Given
        Car car1 = Car.builder()
                .id(1L)
                .carName("Test Car 1")
                .mmScore(75.0)
                .initialRegistration("2020.12.01")
                .mileage(1000L)
                .fuelEfficiency(10.0)
                .build();

        Car car2 = Car.builder()
                .id(2L)
                .carName("Test Car 2")
                .mmScore(30.0)
                .initialRegistration("2020.06.01")
                .mileage(8000L)
                .fuelEfficiency(11.0)
                .build();

        ReflectionTestUtils.setField(car1, "accidentHistoryList", List.of());
        ReflectionTestUtils.setField(car2, "accidentHistoryList", List.of());

        Like like1 = Like.builder()
                .car(car1)
                .userId(TEST_USER_ID)
                .isLike(true)
                .build();
        Like like2 = Like.builder()
                .car(car2)
                .userId(TEST_USER_ID)
                .isLike(true)
                .build();

        List<Long> requestCarIds = List.of(1L, 2L);

        when(carRepository.findAllById(anyList())).thenReturn(List.of(car1, car2));
        when(likeRepository.findByUserIdAndCarId(TEST_USER_ID, 1L)).thenReturn(Optional.of(like1));
        when(likeRepository.findByUserIdAndCarId(TEST_USER_ID, 2L)).thenReturn(Optional.of(like2));

        // When
        LikeComparisonResponse result = likeService.getComparisonLikeCarList(TEST_USER_ID, requestCarIds);

        // Then
        assertThat(result.bestCar())
                .extracting("")
        assertThat(result.cars())
                .extracting("id", "carName")
                .containsExactly(
                        tuple(1L, "Test Car 1"),
                        tuple(2L, "Test Car 2")
                );
    }

    @Test
    void getComparisonLikeCarList_whenCarIdNotFound_throwException() {
        // Given
        List<Long> requestCarIds = List.of(1L, 999L);
        Car car1 = Car.builder().id(1L).build();

        when(carRepository.findAllById(anyList())).thenReturn(List.of(car1));

        // When & Then
        assertThatThrownBy(() -> likeService.getComparisonLikeCarList(TEST_USER_ID, requestCarIds))
                .isInstanceOf(LikeComparisonError.class)
                .hasMessageContaining("carNotFound");
    }

    @Test
    void getComparisonLikeCarList_whenIsLikeFalse_throwException() {
        // Given
        List<Long> requestCarIds = List.of(1L, 2L);
        Car car1 = Car.builder().id(1L).build();
        Car car2 = Car.builder().id(2L).build();

        Like like1 = Like.builder()
                .car(car1)
                .userId(TEST_USER_ID)
                .isLike(true)
                .build();
        Like like2 = Like.builder()
                .car(car2)
                .userId(TEST_USER_ID)
                .isLike(false)  // false case
                .build();

        when(carRepository.findAllById(anyList())).thenReturn(List.of(car1, car2));
        when(likeRepository.findByUserIdAndCarId(TEST_USER_ID, 1L)).thenReturn(Optional.of(like1));
        when(likeRepository.findByUserIdAndCarId(TEST_USER_ID, 2L)).thenReturn(Optional.of(like2));

        // When & Then
        assertThatThrownBy(() -> likeService.getComparisonLikeCarList(TEST_USER_ID, requestCarIds))
                .isInstanceOf(LikeComparisonError.class)
                .hasMessageContaining("isLikeFalse");
    }

    @Test
    void getComparisonLikeCarList_whenFewerThanTwoCarIds_throwException() {
        // Given
        List<Long> requestCarIds = List.of(1L);

        // When & Then
        assertThatThrownBy(() -> likeService.getComparisonLikeCarList(TEST_USER_ID, requestCarIds))
                .isInstanceOf(LikeComparisonError.class)
                .hasMessageContaining("invalidLength");
    }

    @Test
    void getComparisonLikeCarList_whenDuplicatedDataWithMoreThanTwoDistinctData_thenReturnComparisonLikeList() {
        // Given
        List<Long> requestCarIds = List.of(1L, 2L, 2L, 1L);  // 중복된 ID들

        Car car1 = Car.builder()
                .id(1L)
                .carName("Test Car 1")
                .build();
        Car car2 = Car.builder()
                .id(2L)
                .carName("Test Car 2")
                .build();

        Like like1 = Like.builder()
                .car(car1)
                .userId(TEST_USER_ID)
                .isLike(true)
                .build();
        Like like2 = Like.builder()
                .car(car2)
                .userId(TEST_USER_ID)
                .isLike(true)
                .build();

        when(carRepository.findAllById(anyList())).thenReturn(List.of(car1, car2));
        when(likeRepository.findByUserIdAndCarId(TEST_USER_ID, 1L)).thenReturn(Optional.of(like1));
        when(likeRepository.findByUserIdAndCarId(TEST_USER_ID, 2L)).thenReturn(Optional.of(like2));

        // When
        LikeComparisonResponse result = likeService.getComparisonLikeCarList(TEST_USER_ID, requestCarIds);

        // Then
        assertThat(result.cars()).hasSize(2);
        assertThat(result.cars())
                .extracting("id", "carName")
                .containsExactly(
                        tuple(1L, "Test Car 1"),
                        tuple(2L, "Test Car 2")
                );
    }
}
