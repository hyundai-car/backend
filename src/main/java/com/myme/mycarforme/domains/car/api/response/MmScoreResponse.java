package com.myme.mycarforme.domains.car.api.response;

import com.myme.mycarforme.domains.car.dto.MmScoreDto;

import java.util.List;

public record MmScoreResponse(List<MmScoreDto> contents) {
    public static MmScoreResponse from(List<MmScoreDto> mmScoreDto) {
        return new MmScoreResponse(mmScoreDto);
    }
}
