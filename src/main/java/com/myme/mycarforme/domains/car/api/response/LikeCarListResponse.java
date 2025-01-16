package com.myme.mycarforme.domains.car.api.response;

import com.myme.mycarforme.domains.car.domain.Like;
import com.myme.mycarforme.domains.car.dto.LikeCarDto;
import org.springframework.data.domain.Page;

import java.util.List;

public record LikeCarListResponse(
        List<LikeCarDto> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean isFirst,
        boolean isLast
) {
    public static LikeCarListResponse from(Page<Like> page) {
        List<LikeCarDto> likeCarDtos = page.getContent()
                .stream()
                .map(like -> LikeCarDto.from(like, like.getCar()))
                .toList();

        return new LikeCarListResponse(
                likeCarDtos,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}