package com.myme.mycarforme.domains.car.repository;

import com.myme.mycarforme.domains.car.domain.Car;
import com.myme.mycarforme.domains.car.domain.DetailImage;
import com.myme.mycarforme.domains.car.domain.Exterior360Image;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository  extends JpaRepository<Car, Long> {
    @Query("SELECT c FROM Car c " +
            "WHERE c.isOnSale = 1" +
            "AND (:keyword IS NULL OR c.carName LIKE %:keyword% OR c.carNumber LIKE %:keyword%) " +
            "AND (:carType IS NULL OR c.carType IN :carType) " +  // 변경
            "AND (:fuelType IS NULL OR c.fuelType IN :fuelType) " + // 변경
            "AND (:minSellingPrice IS NULL OR c.sellingPrice >= :minSellingPrice) " +
            "AND (:maxSellingPrice IS NULL OR c.sellingPrice <= :maxSellingPrice) " +
            "AND (:minMileage IS NULL OR c.mileage >= :minMileage) " +
            "AND (:maxMileage IS NULL OR c.mileage <= :maxMileage) " +
            "AND (:minYear IS NULL OR c.year >= :minYear) " +
            "AND (:maxYear IS NULL OR c.year <= :maxYear)")
    Page<Car> findAllBySearchCondition(
            @Param("keyword") String keyword,
            @Param("carType") List<String> carType,
            @Param("fuelType") List<String> fuelType,
            @Param("minSellingPrice") Long minSellingPrice,
            @Param("maxSellingPrice") Long maxSellingPrice,
            @Param("minMileage") Long minMileage,
            @Param("maxMileage") Long maxMileage,
            @Param("minYear") Long minYear,
            @Param("maxYear") Long maxYear,
            Pageable pageable
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

    @Query("SELECT c FROM Car c WHERE c.isOnSale = 1 ORDER BY c.mmScore DESC LIMIT 5")
    List<Car> findTop5ByOrderByMmScoreDesc();

    List<Car> findByBuyerId(String userId);

    List<Car> findByBuyerIdAndIsOnSaleNot(String userId, int isOnSale);

    @Query("SELECT c FROM Car c " +
            "WHERE c.isOnSale = 1 " +
            "ORDER BY (SELECT COUNT(l) FROM Like l WHERE l.car = c AND l.isLike = true) DESC " +
            "LIMIT 5")
    List<Car> findTop5ByOrderByLikeCountDesc();

    @Query("SELECT c FROM Car c WHERE c.isOnSale = 0 ORDER BY c.id DESC LIMIT 5")
    List<Car> findTop5UpcomingCarsByOrderByIdDesc();

    List<Car> findBySellingPriceLessThanEqualAndIsOnSale(Long maxPrice, Integer isOnSale);

    @Query("""
    SELECT c.buyerId FROM Car c 
    WHERE c.paymentDeliveryStatus = :status 
    GROUP BY c.buyerId 
    ORDER BY MAX(c.payedAt) DESC
    """)
    List<String> findDistinctBuyerIdsByPaymentDeliveryStatus(@Param("status") int status);
}
