package com.myme.mycarforme.domains.car.repository;

import com.myme.mycarforme.domains.car.domain.Car;
import com.myme.mycarforme.domains.car.domain.DetailImage;
import com.myme.mycarforme.domains.car.domain.Exterior360Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository  extends JpaRepository<Car, Long> {
    @Query("SELECT c FROM Car c " +
            "WHERE (:keyword IS NULL OR c.carName LIKE %:keyword% OR c.carNumber LIKE %:keyword%) " +
            "AND (:carType IS NULL OR c.carType IN :carType) " +  // 변경
            "AND (:fuelType IS NULL OR c.fuelType IN :fuelType) " + // 변경
            "AND (:minSellingPrice IS NULL OR c.sellingPrice >= :minSellingPrice) " +
            "AND (:maxSellingPrice IS NULL OR c.sellingPrice <= :maxSellingPrice) " +
            "AND (:minMileage IS NULL OR c.mileage >= :minMileage) " +
            "AND (:maxMileage IS NULL OR c.mileage <= :maxMileage) " +
            "AND (:minYear IS NULL OR c.year >= :minYear) " +
            "AND (:maxYear IS NULL OR c.year <= :maxYear)")
    List<Car> findAllBySearchCondition(
            @Param("keyword") String keyword,
            @Param("carType") List<String> carType,
            @Param("fuelType") List<String> fuelType,
            @Param("minSellingPrice") Long minSellingPrice,
            @Param("maxSellingPrice") Long maxSellingPrice,
            @Param("minMileage") Long minMileage,
            @Param("maxMileage") Long maxMileage,
            @Param("minYear") String minYear,
            @Param("maxYear") String maxYear
    );

    @Query("SELECT c FROM Car c " +
            "LEFT JOIN FETCH c.optionList " +
            "LEFT JOIN FETCH c.accidentHistoryList " +
            "WHERE c.id = :carId")
    Optional<Car> findByIdWithDetails(@Param("carId") Long carId);


    @Query("SELECT e FROM Exterior360Image e WHERE e.car.id = :carId ORDER BY e.rotationDegree")
    List<Exterior360Image> findAllByCarIdOrderByRotationDegree(@Param("carId") Long carId);

    @Query("SELECT d FROM DetailImage d WHERE d.car.id = :carId")
    List<DetailImage> findAllDetailImagesByCarId(@Param("carId") Long carId);




}
