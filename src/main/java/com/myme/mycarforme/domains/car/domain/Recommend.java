package com.myme.mycarforme.domains.car.domain;

import com.myme.mycarforme.global.common.jpa.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "recommends")
public class Recommend extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id")
    private Car car;

    private LocalDateTime recommendedAt;
    private Long recommendPriority;
    private String recommendCondition;
    private String recommendReason;

    @Builder
    private Recommend( Car car, LocalDateTime recommendedAt, Long recommendPriority, String recommendCondition, String recommendReason ) {
        this.car = car;
        this.recommendedAt = recommendedAt;
        this.recommendPriority = recommendPriority;
        this.recommendCondition = recommendCondition;
        this.recommendReason = recommendReason;
    }





}
