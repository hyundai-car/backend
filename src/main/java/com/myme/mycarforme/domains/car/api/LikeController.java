package com.myme.mycarforme.domains.car.api;

import com.myme.mycarforme.domains.car.api.response.LikeResponse;
import com.myme.mycarforme.domains.car.service.LikeService;
import com.myme.mycarforme.global.common.response.CommonResponse;
import com.myme.mycarforme.global.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/{carId}")
    public CommonResponse<LikeResponse> toggleLike(@PathVariable("carId") Long carId) {
        String userId = SecurityUtil.getUserId();
        return CommonResponse.from(likeService.toggleLike(userId, carId));
    }

    @GetMapping("/{carId}")
    public CommonResponse<LikeResponse> getLikeByCarId(@PathVariable("carId") Long carId) {
        String userId = SecurityUtil.getUserId();
        return CommonResponse.from(likeService.getLikeByCarId(userId, carId));
    }
}
