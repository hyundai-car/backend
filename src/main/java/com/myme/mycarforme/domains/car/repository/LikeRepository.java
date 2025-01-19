package com.myme.mycarforme.domains.car.repository;

import com.myme.mycarforme.domains.admin.dto.ActivityEntityDto;
import com.myme.mycarforme.domains.car.domain.Like;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserIdAndCarId(String userId, Long carId);

    List<Like> findByUserIdAndCarIdIn(String userId, Set<Long> carIdList);

    @Query("SELECT l FROM Like l " +
            "JOIN FETCH l.car c " +
            "WHERE l.userId = :userId " +
            "AND l.isLike = true")
    Page<Like> findCarsByUserIdAndIsLikeTrue(@Param("userId") String userId, Pageable pageable);


    @Query("SELECT COUNT(l) FROM Like l WHERE l.car.id = :carId AND l.isLike = true")
    Long countByCarIdAndIsLikeTrue(@Param("carId") Long carId);

    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Like l " +
            "WHERE l.car.id = :carId AND l.userId = :userId AND l.isLike = true")
    Boolean existsByCarIdAndUserIdAndIsLikeTrue(@Param("carId") Long carId, @Param("userId") String userId);

    List<Like> findTop10ByOrderByUpdatedAtDesc();
}
