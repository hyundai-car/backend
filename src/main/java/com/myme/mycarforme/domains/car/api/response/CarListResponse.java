package com.myme.mycarforme.domains.car.api.response;

import com.myme.mycarforme.domains.car.domain.Car;
import com.myme.mycarforme.domains.car.dto.CarDto;
import org.springframework.data.domain.Page;

import java.util.List;

public record CarListResponse(
        List<CarDto> contents,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean isFirst,
        boolean isLast                       )
{
    public static CarListResponse of(Page<CarDto> page) {
        return new CarListResponse(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}
