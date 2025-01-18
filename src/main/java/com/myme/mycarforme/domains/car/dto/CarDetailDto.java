package com.myme.mycarforme.domains.car.dto;

import com.myme.mycarforme.domains.car.domain.Car;
import com.myme.mycarforme.global.util.helper.CalculateHelper.CarScoreNormalizer;
import com.myme.mycarforme.global.util.helper.DateFormatHelper;

import java.util.List;
import java.util.stream.Collectors;

public record CarDetailDto(Long carId,
                           String carName,
                           String initialRegistration,
                           Long mileage,
                           Long sellingPrice,
                           Double mmScore,
                           String exteriorColor,
                           String interiorColor,
                           Long displacement,
                           String fuelType,
                           String transmissionType,
                           String location,
                           Double fuelEfficiency,
                           String mainImage,
                           Long newCarPrice,
                           String carNumber,
                           Long seating,
                           String createdAt,
                           String updatedAt,
                           OptionListDto optionLists,
                           List<AccidentHistoryDto> accidentHistoryList,
                           Integer accidentCount,
                           ComparisonGraphItemDto graph
) {

    public static CarDetailDto from(Car car) {
        List<AccidentHistoryDto> accidentHistoryList = car.getAccidentHistoryList().stream()
                .map(AccidentHistoryDto::from)
                .collect(Collectors.toList());

        return new CarDetailDto(
                car.getId(),
                car.getCarName(),
                DateFormatHelper.toKoreanDateString(car.getInitialRegistration()),
                car.getMileage(),
                car.getSellingPrice(),
                car.getMmScore(),
                car.getExteriorColor(),
                car.getInteriorColor(),
                car.getDisplacement(),
                car.getFuelType(),
                car.getTransmissionType(),
                car.getLocation(),
                (car.getCityEfficiency() + car.getHighwayEfficiency())/2,
                car.getMainImage(),
                car.getNewCarPrice(),
                car.getCarNumber(),
                car.getSeating(),
                car.getCreatedAt() != null ? car.getCreatedAt().toString() : null,
                car.getUpdatedAt() != null ? car.getUpdatedAt().toString() : null,
                car.getOptionList() != null ? OptionListDto.from(car.getOptionList()) : null,
                accidentHistoryList,
                accidentHistoryList.size(),
                ComparisonGraphItemDto.builder()
                        .mmScoreNorm(car.getMmScore())
                        .accidentCountNorm(CarScoreNormalizer.normalizeAccidentCount(car.getAccidentHistoryList().size()))
                        .initialRegistrationNorm(CarScoreNormalizer.normalizeInitialRegistration(car.getInitialRegistration()))
                        .mileageNorm(CarScoreNormalizer.normalizeMileage(car.getMileage()))
                        .fuelEfficiencyNorm(CarScoreNormalizer.normalizeFuelEfficiency((car.getCityEfficiency() + car.getHighwayEfficiency()) / 2))
                        .build()
        );
    }

}
