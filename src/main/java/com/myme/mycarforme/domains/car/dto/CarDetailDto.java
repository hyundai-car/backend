package com.myme.mycarforme.domains.car.dto;

import com.myme.mycarforme.domains.car.domain.Car;

import java.util.List;
import java.util.stream.Collectors;

public record CarDetailDto(Long carId,
                           String carName,
                           Long year,
                           Long mileage,
                           Long sellingPrice,
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
                           List<AccidentHistoryDto> accidentHistoryList) {

    public static CarDetailDto from(Car car) {
        return new CarDetailDto(
                car.getId(),
                car.getCarName(),
                car.getYear(),
                car.getMileage(),
                car.getSellingPrice(),
                car.getExteriorColor(),
                car.getInteriorColor(),
                car.getDisplacement(),
                car.getFuelType(),
                car.getTransmissionType(),
                car.getLocation(),
                car.getFuelEfficiency(),
                car.getMainImage(),
                car.getNewCarPrice(),
                car.getCarNumber(),
                car.getSeating(),
                car.getCreatedAt() != null ? car.getCreatedAt().toString() : null,
                car.getUpdatedAt() != null ? car.getUpdatedAt().toString() : null,
                car.getOptionList() != null ? OptionListDto.from(car.getOptionList()) : null,
                car.getAccidentHistoryList().stream()
                        .map(AccidentHistoryDto::from)
                        .collect(Collectors.toList())
        );
    }

}
