package com.myme.mycarforme.domains.car.dto;

import com.myme.mycarforme.domains.car.domain.Car;

import java.util.List;
import java.util.stream.Collectors;

public record CarDetailDto(Long carId,
                           String modelName,
                           String year,
                           Long mileage,
                           Long sellingPrice,
                           String color,
                           Long displacement,
                           String fuelType,
                           String transmissionType,
                           String location,
                           Double fuelEfficiency,
                           String mainImage,
                           Long newCarPrice,
                           String carNumber,
                           String createdAt,
                           String updatedAt,
                           OptionListDto optionLists,
                           List<AccidentHistoryDto> accidentHistories) {

    public static CarDetailDto from(Car car) {
        return new CarDetailDto(
                car.getId(),
                car.getCarName(),
                car.getYear().toString(),
                car.getMileage(),
                car.getSellingPrice(),
                car.getExteriorColor(),
                car.getDisplacement(),
                car.getFuelType(),
                car.getTransmissionType(),
                car.getLocation(),
                car.getFuelEfficiency(),
                car.getMainImage(),
                car.getNewCarPrice(),
                car.getCarNumber(),
                car.getCreatedAt() != null ? car.getCreatedAt().toString() : null,
                car.getUpdatedAt() != null ? car.getUpdatedAt().toString() : null,
                OptionListDto.from(car.getOptionList()),
                car.getAccidentHistories().stream()
                        .map(AccidentHistoryDto::from)
                        .collect(Collectors.toList())
        );
    }

}
