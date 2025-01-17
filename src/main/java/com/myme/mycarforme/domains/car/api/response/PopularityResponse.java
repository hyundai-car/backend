package com.myme.mycarforme.domains.car.api.response;

import com.myme.mycarforme.domains.car.dto.PopularityDto;
import java.util.List;

public record PopularityResponse(List<PopularityDto> contents) {
    public static PopularityResponse from(List<PopularityDto> popularityDto) {
        return new PopularityResponse(popularityDto);
    }

}
