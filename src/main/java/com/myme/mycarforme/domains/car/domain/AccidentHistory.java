package com.myme.mycarforme.domains.car.domain;

import com.myme.mycarforme.global.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="accident_histories")
public class AccidentHistory extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id")
    private Car car;

    private LocalDateTime accidentDate;
    private Long carPartsPrice;
    private Long carLaborPrice;
    private Long carPaintPrice;


    @Builder
    private AccidentHistory( Car car, LocalDateTime accidentDate, Long carPartsPrice, Long carLaborPrice, Long carPaintPrice) {
        this.car = car;
        this.accidentDate = accidentDate;
        this.carPartsPrice = carPartsPrice;
        this.carLaborPrice = carLaborPrice;
        this.carPaintPrice = carPaintPrice;
    }



}
