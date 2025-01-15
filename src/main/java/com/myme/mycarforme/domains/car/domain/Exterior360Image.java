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
@Table(name="exterior_360_images")
public class Exterior360Image extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id")
    private Car car;

    private String imageUrl;
    private Integer rotationDegree;

    @Builder
    private Exterior360Image(Car car, String imageUrl, Integer rotationDegree) {
        this.car = car;
        this.imageUrl = imageUrl;
        this.rotationDegree = rotationDegree;
    }


}
