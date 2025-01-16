package com.myme.mycarforme.domains.car.repository;

import com.myme.mycarforme.domains.car.domain.Car;
import com.myme.mycarforme.domains.car.domain.Like;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserIdAndCarId(String userId, Long carId);

    @Query("SELECT l FROM Like l " +
            "JOIN FETCH l.car c " +
            "WHERE l.userId = :userId " +
            "AND l.isLike = true")
    Page<Like> findCarsByUserIdAndIsLikeTrue(@Param("userId") String userId, Pageable pageable);
}
