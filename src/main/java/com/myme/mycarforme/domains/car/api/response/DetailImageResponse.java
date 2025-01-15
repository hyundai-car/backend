package com.myme.mycarforme.domains.car.api.response;

import com.myme.mycarforme.domains.car.dto.DetailImageDto;

import java.util.List;

public record DetailImageResponse(List<DetailImageDto> contents) {
    public static DetailImageResponse from(List<DetailImageDto> images) {
        return new DetailImageResponse(images);
    }
}
