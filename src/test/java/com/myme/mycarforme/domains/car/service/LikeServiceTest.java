package com.myme.mycarforme.domains.car.service;

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
    }

    @Test
    void toggleLike_whenValidCar_thenReturnTrue() {
        // Given
        Like newLike = Like.builder()
                .id(1L)
                .userId(TEST_USER_ID)
                .car(TEST_CAR)
                .isLike(true)
                .build();

        when(carRepository.findById(TEST_CAR_ID)).thenReturn(Optional.of(TEST_CAR));
        when(likeRepository.findByUserIdAndCarId(TEST_USER_ID, TEST_CAR_ID)).thenReturn(Optional.empty());
        when(likeRepository.save(any(Like.class))).thenReturn(newLike);

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
        Like existingLike = Like.builder()
                .id(1L)
                .userId(TEST_USER_ID)
                .car(TEST_CAR)
                .isLike(true)
                .build();

        when(carRepository.findById(TEST_CAR_ID)).thenReturn(Optional.of(TEST_CAR));
        when(likeRepository.findByUserIdAndCarId(TEST_USER_ID, TEST_CAR_ID)).thenReturn(Optional.of(existingLike));

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
        Like existingLike = Like.builder()
                .id(1L)
                .userId(TEST_USER_ID)
                .car(TEST_CAR)
                .isLike(false)
                .build();

        when(carRepository.findById(TEST_CAR_ID)).thenReturn(Optional.of(TEST_CAR));
        when(likeRepository.findByUserIdAndCarId(TEST_USER_ID, TEST_CAR_ID)).thenReturn(Optional.of(existingLike));

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
        Like existingLike = Like.builder()
                .id(1L)
                .userId(TEST_USER_ID)
                .car(TEST_CAR)
                .isLike(true)
                .build();

        when(carRepository.findById(TEST_CAR_ID)).thenReturn(Optional.of(TEST_CAR));
        when(likeRepository.findByUserIdAndCarId(TEST_USER_ID, TEST_CAR_ID)).thenReturn(Optional.of(existingLike));

        // When
        LikeResponse result = likeService.getLikeByCarId(TEST_USER_ID, TEST_CAR_ID);

        // Then
        assertThat(result.carId()).isEqualTo(TEST_CAR_ID);
        assertThat(result.isLike()).isTrue();
    }

    @Test
    void getLikeByCarId_whenCarExistAndLikeIsFalse_thenReturnLike() {
        // Given
        Like existingLike = Like.builder()
                .id(1L)
                .userId(TEST_USER_ID)
                .car(TEST_CAR)
                .isLike(false)
                .build();

        when(carRepository.findById(TEST_CAR_ID)).thenReturn(Optional.of(TEST_CAR));
        when(likeRepository.findByUserIdAndCarId(TEST_USER_ID, TEST_CAR_ID)).thenReturn(Optional.of(existingLike));

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
}
