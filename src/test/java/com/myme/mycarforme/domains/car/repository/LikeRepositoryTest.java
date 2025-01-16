package com.myme.mycarforme.domains.car.repository;

import com.myme.mycarforme.domains.car.domain.Car;
import com.myme.mycarforme.domains.car.domain.Like;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class LikeRepositoryTest {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private CarRepository carRepository;

    private Pageable pageable;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void findCarsByUserIdAndIsLikeTrue_whenValidData_thenReturnLikeList() {
        // given
        Car car = Car.builder()
                .carName("Test car")
                .build();
        carRepository.save(car);

        Like like = Like.builder()
                .userId("testUser")
                .car(car)
                .isLike(true)
                .build();
        likeRepository.save(like);

        // when
        Page<Car> cars = likeRepository.findCarsByUserIdAndIsLikeTrue("testUser", pageable);

        // then
        assertThat(cars.getContent())
                .isNotEmpty()
                .hasSize(1);

        Car resultCar = cars.getContent().get(0);
        assertThat(resultCar.getCarName()).isEqualTo("Test car");
    }

    @Test
    void findCarsByUserIdAndIsLikeTrue_whenDataEmpty_thenReturnEmpty() {
        // when
        Page<Car> cars = likeRepository.findCarsByUserIdAndIsLikeTrue("nonexistentUser", pageable);

        // then
        assertThat(cars.getContent()).isEmpty();
    }

    @Test
    void findCarsByUserIdAndIsLikeTrue_whenIsLikeFalse_thenDataNotIncluded() {
        // given
        Car car = Car.builder()
                .carName("Test car")
                .build();
        carRepository.save(car);

        Like like = Like.builder()
                .userId("testUser")
                .car(car)
                .isLike(false)
                .build();
        likeRepository.save(like);  // Like 엔티티 저장 추가

        // when
        Page<Car> cars = likeRepository.findCarsByUserIdAndIsLikeTrue("testUser", pageable);

        // then
        assertThat(cars.getContent()).isEmpty();
    }
}