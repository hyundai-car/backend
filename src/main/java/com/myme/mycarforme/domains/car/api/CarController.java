package com.myme.mycarforme.domains.car.api;

import com.myme.mycarforme.domains.car.api.request.CarSearchRequest;
import com.myme.mycarforme.domains.car.api.response.*;
import com.myme.mycarforme.domains.car.dto.CarDetailDto;
import com.myme.mycarforme.domains.car.dto.DetailImageDto;
import com.myme.mycarforme.domains.car.dto.Exterior360ImageDto;
import com.myme.mycarforme.domains.car.dto.MmScoreDto;
import com.myme.mycarforme.domains.car.service.CarService;
import com.myme.mycarforme.global.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cars")
public class CarController {
    private final CarService carService;

    @GetMapping
    public CommonResponse<CarListResponse> searchCars(CarSearchRequest request) {
        CarListResponse response = CarListResponse.of(carService.searchCars(request));
        return CommonResponse.from(response);
    }

    @GetMapping("/{carId}")
    public CommonResponse<CarDetailResponse> getCarDetail(@PathVariable Long carId) {
        CarDetailDto carDetailDto = carService.getCarDetail(carId);
        return CommonResponse.from(CarDetailResponse.from(carDetailDto));
    }

    @GetMapping("/{carId}/360images")
    public CommonResponse<Exterior360ImageResponse> get360Images(@PathVariable Long carId) {
        List<Exterior360ImageDto> images = carService.get360Images(carId);
        return CommonResponse.from(Exterior360ImageResponse.from(images));
    }

    @GetMapping("/{carId}/detailimages")
    public CommonResponse<DetailImageResponse> getDetailImages(@PathVariable Long carId) {
        List<DetailImageDto> images = carService.getDetailImages(carId);
        return CommonResponse.from(DetailImageResponse.from(images));
    }

    @GetMapping("/mmscores")
    public CommonResponse<MmScoreResponse> getMmscores() {
        MmScoreResponse response = carService.getTop5CarsByMmScore();
        return CommonResponse.from(response);
    }



}
