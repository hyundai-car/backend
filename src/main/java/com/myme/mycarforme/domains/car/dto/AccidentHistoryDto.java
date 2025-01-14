package com.myme.mycarforme.domains.car.dto;

import com.myme.mycarforme.domains.car.domain.AccidentHistory;

import java.time.LocalDateTime;

public record AccidentHistoryDto(Long accidentHistoryId,
                                 String accidentDate,
                                 Long carPartsPrice,
                                 Long carLaborPrice,
                                 Long carPaintPrice,
                                 String createdAt,
                                 String updatedAt) {

    public static AccidentHistoryDto from(AccidentHistory accidentHistory) {
        return new AccidentHistoryDto(
                accidentHistory.getId(),
                accidentHistory.getAccidentDate().toString(),
                accidentHistory.getCarPartsPrice(),
                accidentHistory.getCarLaborPrice(),
                accidentHistory.getCarPaintPrice(),
                accidentHistory.getCreatedAt().toString(),
                accidentHistory.getUpdatedAt().toString()
        );
    }

}
