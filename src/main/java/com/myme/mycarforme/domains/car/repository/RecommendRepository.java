package com.myme.mycarforme.domains.car.repository;

import com.myme.mycarforme.domains.car.domain.Car;
import com.myme.mycarforme.domains.car.domain.Recommend;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecommendRepository extends JpaRepository<Recommend, Long> {
    List<Recommend> findByCarId(Long carId);
    List<Recommend> findTop5ByOrderByRecommendPriorityAsc();

    @Query("SELECT r FROM Recommend r " +
            "WHERE r.userId=:userId " +
            "ORDER BY r.createdAt DESC, r.recommendPriority ASC " +
            "LIMIT 10")
    List<Recommend> findTop10RecommendHistory(@Param("userId") String userId);


}
