package com.myme.mycarforme.domains.car.dto;

import com.myme.mycarforme.domains.car.domain.DetailImage;

public record DetailImageDto(Long detailImageId,
                             String imageUrl,
                             String createdAt,
                             String updatedAt) {

    public static DetailImageDto from(DetailImage detailImage) {
        return new DetailImageDto(
          detailImage.getId(),
          detailImage.getImageUrl(),
          detailImage.getCreatedAt() != null ? detailImage.getCreatedAt().toString() : null,
          detailImage.getUpdatedAt() != null ? detailImage.getUpdatedAt().toString() : null
        );
    }
}
