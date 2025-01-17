package com.myme.mycarforme.domains.car.repository;

import com.myme.mycarforme.domains.car.domain.Car;
import com.myme.mycarforme.domains.car.domain.Recommend;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendRepository extends JpaRepository<Recommend, Long> {
    List<Recommend> findByCarId(Long carId);
    List<Recommend> findTop5ByOrderByRecommendPriorityAsc();



}
