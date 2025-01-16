package com.myme.mycarforme.domains.car.api.request;


import java.util.List;

public record CarSearchRequest(String keyword, List<String> carType, List<String> fuelType,
                               Long minSellingPrice, Long maxSellingPrice,
                               Long minMileage, Long maxMileage,
                               Long minYear, Long maxYear) {

    public static CarSearchRequest of(
            String keyword, List<String> carType, List<String> fuelType,
            Long minSellingPrice, Long maxSellingPrice,
            Long minMileage, Long maxMileage,
            Long minYear, Long maxYear) {
        return new CarSearchRequest(
                keyword, carType, fuelType,
                minSellingPrice, maxSellingPrice,
                minMileage, maxMileage,
                minYear, maxYear);
    }

    // 단일 값을 받아서 리스트로 변환하는 새로운 팩토리 메서드 추가
    public static CarSearchRequest ofSingle(
            String keyword,
            String carType,          // 단일 값
            String fuelType,         // 단일 값
            Long minSellingPrice,
            Long maxSellingPrice,
            Long minMileage,
            Long maxMileage,
            Long minYear,
            Long maxYear) {
        return new CarSearchRequest(
                keyword,
                carType != null ? List.of(carType) : null,     // 단일 값을 리스트로 변환
                fuelType != null ? List.of(fuelType) : null,   // 단일 값을 리스트로 변환
                minSellingPrice,
                maxSellingPrice,
                minMileage,
                maxMileage,
                minYear,
                maxYear);
    }


}







