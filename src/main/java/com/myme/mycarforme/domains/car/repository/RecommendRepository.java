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

    @Query(value = "SELECT * FROM recommends r " +
            "ORDER BY r.created_at DESC, r.recommend_priority ASC " +
            "LIMIT 10",
            nativeQuery = true)
    List<Recommend> findTop10RecommendHistory();


}
