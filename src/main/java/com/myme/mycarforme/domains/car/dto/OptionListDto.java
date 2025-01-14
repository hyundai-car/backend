package com.myme.mycarforme.domains.car.dto;

import com.myme.mycarforme.domains.car.domain.OptionList;

public record OptionListDto(Long optionListId,
                            Boolean hasNavigation,
                            Boolean hasHiPass,
                            Boolean hasHeatedSteeringWheel,
                            Boolean hasHeatedSeats,
                            Boolean hasVentilatedFrontSeats,
                            Boolean hasPowerFrontSeats,
                            Boolean isLeatherSeats,
                            Boolean hasPowerTrunk,
                            Boolean hasSunroof,
                            Boolean hasHeadUpDisplay,
                            Boolean hasSurroundViewMonitor,
                            Boolean hasRearViewMonitor,
                            Boolean hasBlindSpotWarning,
                            Boolean hasLaneDepartureWarning,
                            Boolean hasSmartCruiseControl,
                            Boolean hasFrontParkingSensors,
                            String createdAt,
                            String updatedAt) {

    public static OptionListDto from(OptionList optionList) {
        return new OptionListDto(
                optionList.getId(),
                optionList.getHasNavigation(),
                optionList.getHasHiPass(),
                optionList.getHasHeatedSteeringWheel(),
                optionList.getHasHeatedSeats(),
                optionList.getHasVentilatedFrontSeats(),
                optionList.getHasPowerFrontSeats(),
                optionList.getIsLeatherSeats(),
                optionList.getHasPowerTrunk(),
                optionList.getHasSunroof(),
                optionList.getHasHeadUpDisplay(),
                optionList.getHasSurroundViewMonitor(),
                optionList.getHasRearViewMonitor(),
                optionList.getHasBlindSpotWarning(),
                optionList.getHasLaneDepartureWarning(),
                optionList.getHasSmartCruiseControl(),
                optionList.getHasFrontParkingSensors(),
                optionList.getCreatedAt().toString(),
                optionList.getUpdatedAt().toString()
        );
    }
}
