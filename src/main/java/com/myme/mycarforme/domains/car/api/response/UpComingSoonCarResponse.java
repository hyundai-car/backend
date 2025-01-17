package com.myme.mycarforme.domains.car.api.response;

import com.myme.mycarforme.domains.car.dto.UpComingSoonCarDto;
import java.util.List;

public record UpComingSoonCarResponse(List<UpComingSoonCarDto> contents) {
    public static UpComingSoonCarResponse from(List<UpComingSoonCarDto> upComingSoonCarDto) {
        return new UpComingSoonCarResponse(upComingSoonCarDto);
    }
}
