package com.myme.mycarforme.domains.car.api;

import com.myme.mycarforme.domains.car.api.request.RecommendRequest;
import com.myme.mycarforme.domains.car.api.response.RecommendHistoryResponse;
import com.myme.mycarforme.domains.car.api.response.RecommendResponse;
import com.myme.mycarforme.domains.car.service.RecommendService;
import com.myme.mycarforme.global.common.response.CommonResponse;
import com.myme.mycarforme.global.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommendations")
public class RecommendController {
    private final RecommendService recommendService;

    @PostMapping("/conditions")
    public CommonResponse<RecommendResponse> recommendCars(@RequestBody RecommendRequest request) {
        String userId = SecurityUtil.getUserId();
        RecommendResponse response = recommendService.recommendCars(userId, request);
        return CommonResponse.from(response);
    }

    @GetMapping("/history")
    public CommonResponse<RecommendHistoryResponse> getRecommendHistory() {
        String userId = SecurityUtil.getUserId();
        RecommendHistoryResponse response = recommendService.getRecommendHistory(userId);
        return CommonResponse.from(response);
    }

}
