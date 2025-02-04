package com.myme.mycarforme.domains.car.domain;

import com.myme.mycarforme.global.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="cars")
public class Car extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String buyerId;

    private String carName;
    private String carType;

    @Column(name = "`year`")
    private Long year;
    private String initialRegistration;
    private Long mileage;
    private String driveType;
    private Long displacement;
    private Long sellingPrice;
    private String exteriorColor;
    private String interiorColor;
    private Long seating;
    private String fuelType;
    private String transmissionType;
    private Integer isOnSale;
    private String location;
    private Double mmScore;
    private String mainImage;
    private Long newCarPrice;
    private Double savingAccount;
    private String carNumber;
    private Double accidentSeverity;
    private Double repairProbability;
    private Double predictedPrice;
    private Double cityEfficiency;
    private Double highwayEfficiency;
    private Integer paymentDeliveryStatus;
    private LocalDateTime contractedAt;
    private LocalDateTime payedAt;
    private LocalDateTime deliveryStartedAt;
    private LocalDateTime deliveryEndedAt;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL)
    private List<AccidentHistory> accidentHistoryList = new ArrayList<>();;

    @OneToOne(mappedBy = "car", cascade = CascadeType.ALL)
    private OptionList optionList;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL)
    private List<Like> likeList;
//
//    @OneToMany(mappedBy = "car")
//    private List<Visit> visitList;
//
    @OneToMany(mappedBy = "car")
    private List<Recommend> recommendList;
//
    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL)
    private List<Exterior360Image> exterior360ImageList;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL)
    private List<DetailImage> detailImageList;

    @Builder
    private Car(Long id, String carName, String carType, Long year, String initialRegistration,
                Long mileage, String driveType, Long displacement, Long sellingPrice,
                String exteriorColor, String interiorColor, Long seating, String fuelType,
                String transmissionType, Integer isOnSale, String location, Double mmScore, String mainImage, Long newCarPrice, Double savingAccount,
                String carNumber, Double accidentSeverity, Double repairProbability, Double predictedPrice,
                Double cityEfficiency, Double highwayEfficiency, Integer paymentDeliveryStatus,
                LocalDateTime contractedAt, LocalDateTime payedAt, LocalDateTime deliveryStartedAt,
                LocalDateTime deliveryEndedAt, OptionList optionList) {
        this.id = id;
        this.carName = carName;
        this.carType = carType;
        this.year = year;
        this.initialRegistration = initialRegistration;
        this.mileage = mileage;
        this.driveType = driveType;
        this.displacement = displacement;
        this.sellingPrice = sellingPrice;
        this.exteriorColor = exteriorColor;
        this.interiorColor = interiorColor;
        this.seating = seating;
        this.fuelType = fuelType;
        this.transmissionType = transmissionType;
        this.isOnSale = isOnSale;
        this.location = location;
        this.mmScore = mmScore;
        this.mainImage = mainImage;
        this.newCarPrice = newCarPrice;
        this.savingAccount = savingAccount;
        this.carNumber = carNumber;
        this.accidentSeverity = accidentSeverity;
        this.repairProbability = repairProbability;
        this.predictedPrice = predictedPrice;
        this.cityEfficiency = cityEfficiency;
        this.highwayEfficiency = highwayEfficiency;
        this.paymentDeliveryStatus = paymentDeliveryStatus;
        this.contractedAt = contractedAt;
        this.payedAt = payedAt;
        this.deliveryStartedAt = deliveryStartedAt;
        this.deliveryEndedAt = deliveryEndedAt;
        this.optionList = optionList;
    }

    public void resetOrderStatus() {
        this.paymentDeliveryStatus = 0;
        this.isOnSale = 1;
        this.buyerId = null;
        this.contractedAt = null;
        this.payedAt = null;
        this.deliveryStartedAt = null;
        this.deliveryEndedAt = null;
    }

    public void doContract(String userId) {
        this.paymentDeliveryStatus = 1;
        this.buyerId = userId;
        this.contractedAt = LocalDateTime.now();
    }

    public void doPay() {
        this.paymentDeliveryStatus = 2;
        this.isOnSale = 2;
        this.payedAt = LocalDateTime.now();
    }

    public void doDeliveryStarted() {
        this.paymentDeliveryStatus = 3;
        this.deliveryStartedAt = LocalDateTime.now();
    }

    public void doDeliveryEnded() {
        this.paymentDeliveryStatus = 4;
        this.isOnSale = 3;
        this.deliveryEndedAt = LocalDateTime.now();
    }
}
