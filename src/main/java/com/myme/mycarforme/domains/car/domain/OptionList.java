package com.myme.mycarforme.domains.car.domain;

import com.myme.mycarforme.global.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="option_lists")
public class OptionList extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id")
    private Car car;

    private Boolean hasNavigation;
    private Boolean hasHiPass;
    private Boolean hasHeatedSteeringWheel;
    private Boolean hasHeatedSeats;
    private Boolean hasVentilatedFrontSeats;
    private Boolean hasPowerFrontSeats;
    private Boolean isLeatherSeats;
    private Boolean hasPowerTrunk;
    private Boolean hasSunroof;
    private Boolean hasHeadUpDisplay;
    private Boolean hasSurroundViewMonitor;
    private Boolean hasRearViewMonitor;
    private Boolean hasBlindSpotWarning;
    private Boolean hasLaneDepartureWarning;
    private Boolean hasSmartCruiseControl;
    private Boolean hasFrontParkingSensors;



    @Builder
    private OptionList(Car car, Boolean hasNavigation, Boolean hasHiPass, Boolean hasHeatedSteeringWheel,
                       Boolean hasHeatedSeats, Boolean hasVentilatedFrontSeats, Boolean hasPowerFrontSeats,
                       Boolean isLeatherSeats, Boolean hasPowerTrunk, Boolean hasSunroof, Boolean hasHeadUpDisplay,
                       Boolean hasSurroundViewMonitor, Boolean hasRearViewMonitor, Boolean hasBlindSpotWarning,
                       Boolean hasLaneDepartureWarning, Boolean hasSmartCruiseControl, Boolean hasFrontParkingSensors) {
        this.car = car;
        this.hasNavigation = hasNavigation;
        this.hasHiPass = hasHiPass;
        this.hasHeatedSteeringWheel = hasHeatedSteeringWheel;
        this.hasHeatedSeats = hasHeatedSeats;
        this.hasVentilatedFrontSeats = hasVentilatedFrontSeats;
        this.hasPowerFrontSeats = hasPowerFrontSeats;
        this.isLeatherSeats = isLeatherSeats;
        this.hasPowerTrunk = hasPowerTrunk;
        this.hasSunroof = hasSunroof;
        this.hasHeadUpDisplay = hasHeadUpDisplay;
        this.hasSurroundViewMonitor = hasSurroundViewMonitor;
        this.hasRearViewMonitor = hasRearViewMonitor;
        this.hasBlindSpotWarning = hasBlindSpotWarning;
        this.hasLaneDepartureWarning = hasLaneDepartureWarning;
        this.hasSmartCruiseControl = hasSmartCruiseControl;
        this.hasFrontParkingSensors = hasFrontParkingSensors;
    }





}
