package com.myme.mycarforme.domains.car.dto;

import com.myme.mycarforme.domains.car.domain.Exterior360Image;

public record Exterior360ImageDto(Long exterior360ImageId,
                                  String imageUrl,
                                  Integer rotationDegree,
                                  String createdAt,
                                  String updatedAt  ) {

    public static Exterior360ImageDto from(Exterior360Image exterior360Image) {
        return new Exterior360ImageDto(
                exterior360Image.getId(),
                exterior360Image.getImageUrl(),
                exterior360Image.getRotationDegree(),
                exterior360Image.getCreatedAt() != null ? exterior360Image.getCreatedAt().toString() : null,
                exterior360Image.getUpdatedAt() != null ? exterior360Image.getUpdatedAt().toString() : null
        );
    }
}
