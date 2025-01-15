package com.myme.mycarforme.domains.car.service;

import com.myme.mycarforme.domains.car.domain.Car;
import com.myme.mycarforme.domains.car.domain.Like;
import com.myme.mycarforme.domains.car.domain.OptionList;
import com.myme.mycarforme.domains.car.repository.CarRepository;
import com.myme.mycarforme.domains.car.repository.LikeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
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
                .userId(TEST_USER_ID)
                .car(TEST_CAR)
                .isLike(true)
                .build();

        when(carRepository.existsById(TEST_CAR_ID))
                .thenReturn(true);

        when(likeRepository.findByUserIdAndCarId(TEST_USER_ID, TEST_CAR_ID))
                .thenReturn(Optional.empty());

        when(likeRepository.save(newLike))
                .thenReturn(newLike);

        // When
        boolean result = likeService.toggleLike(TEST_USER_ID, TEST_CAR_ID);

        // Then
        assertThat(result).isTrue();
        verify(likeRepository).existsById(anyLong());
        verify(likeRepository).findByUserIdAndCarId(anyString(), anyLong());
        verify(likeRepository).save(any());
    }

    @Test
    void toggleLike_whenInvalidCar_throwException() {
        // Given
        when(carRepository.existsById(TEST_CAR_ID))
                .thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> likeService.toggleLike(TEST_USER_ID, TEST_CAR_ID))
                .isInstanceOf(CarNotFoundException.class);
        verify(carRepository).existsById(TEST_CAR_ID);
        verify(likeRepository, never()).findByUserIdAndCarId(anyString(), anyLong());
        verify(likeRepository, never()).save(any());
    }

    @Test
    void toggleLike_whenCarExistAndIsLikeTrue_thenReturnFalse() {
        // Given
        Like existingLike = Like.builder()
                .userId(TEST_USER_ID)
                .car(TEST_CAR)
                .isLike(true)
                .build();

        when(carRepository.existsById(TEST_CAR_ID))
                .thenReturn(true);

        when(likeRepository.findByUserIdAndCarId(TEST_USER_ID, TEST_CAR_ID))
                .thenReturn(Optional.of(existingLike));

        // When
        boolean result = likeService.toggleLike(TEST_USER_ID, TEST_CAR_ID);

        // Then
        assertThat(result).isFalse();
        verify(carRepository).existsById(TEST_CAR_ID);
        verify(likeRepository).findByUserIdAndCarId(TEST_USER_ID, TEST_CAR_ID);
        verify(likeRepository, never()).save(any());
    }

    @Test
    void toggleLike_whenCarExistAndIsLikeFalse_thenReturnTrue() {
        // Given
        Like existingLike = Like.builder()
                .userId(TEST_USER_ID)
                .car(TEST_CAR)
                .isLike(false)
                .build();

        Like updatedLike = Like.builder()
                .userId(TEST_USER_ID)
                .car(TEST_CAR)
                .isLike(true)
                .build();

        when(carRepository.existsById(TEST_CAR_ID))
                .thenReturn(true);

        when(likeRepository.findByUserIdAndCarId(TEST_USER_ID, TEST_CAR_ID))
                .thenReturn(Optional.of(existingLike));

        when(likeRepository.save(updatedLike))
                .thenReturn(updatedLike);

        // When
        boolean result = likeService.toggleLike(TEST_USER_ID, TEST_CAR_ID);

        // Then
        assertThat(result).isTrue();
        verify(carRepository).existsById(TEST_CAR_ID);
        verify(likeRepository).findByUserIdAndCarId(TEST_USER_ID, TEST_CAR_ID);
        verify(likeRepository).save(any());
    }
}