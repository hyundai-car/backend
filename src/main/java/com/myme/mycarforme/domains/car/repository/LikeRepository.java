package com.myme.mycarforme.domains.car.repository;

import com.myme.mycarforme.domains.car.domain.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserIdAndCarId(String userId, Long carId);
}
