package com.myme.mycarforme.domains.car.api.response;

import com.myme.mycarforme.domains.car.dto.Exterior360ImageDto;

import java.util.List;

public record Exterior360ImageResponse(List<Exterior360ImageDto> contents) {
    public static Exterior360ImageResponse from(List<Exterior360ImageDto> images) {
        return new Exterior360ImageResponse(images);
    }
}
