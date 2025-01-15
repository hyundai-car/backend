package com.myme.mycarforme.domains.car.api;

import com.myme.mycarforme.domains.car.api.request.CarSearchRequest;
import com.myme.mycarforme.domains.car.api.response.CarDetailResponse;
import com.myme.mycarforme.domains.car.api.response.CarListResponse;
import com.myme.mycarforme.domains.car.dto.CarDetailDto;
import com.myme.mycarforme.domains.car.service.CarService;
import com.myme.mycarforme.global.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cars")
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




}
