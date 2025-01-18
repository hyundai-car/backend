package com.myme.mycarforme.domains.car.api.response;

import com.myme.mycarforme.domains.car.dto.RecommendHistoryDto;
import java.util.List;

public record RecommendHistoryResponse(List<RecommendHistoryDto> contents) {
    public static RecommendHistoryResponse of(List<RecommendHistoryDto> contents) {
        return new RecommendHistoryResponse(contents);
    }

}
