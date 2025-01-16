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
@Table(name="detail_images")
public class DetailImage extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id")
    private Car car;

    private String imageUrl;

    @Builder
    public DetailImage(Car car, String imageUrl) {
        this.car = car;
        this.imageUrl = imageUrl;
    }
}
