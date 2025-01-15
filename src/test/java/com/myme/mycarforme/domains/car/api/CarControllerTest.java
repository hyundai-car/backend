package com.myme.mycarforme.domains.car.api;

import com.myme.mycarforme.domains.car.api.request.CarSearchRequest;
import com.myme.mycarforme.domains.car.dto.AccidentHistoryDto;
import com.myme.mycarforme.domains.car.dto.CarDetailDto;
import com.myme.mycarforme.domains.car.dto.CarDto;
import com.myme.mycarforme.domains.car.dto.OptionListDto;
import com.myme.mycarforme.domains.car.exception.CarNotFoundException;
import com.myme.mycarforme.domains.car.service.CarService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CarController.class)
@WithMockUser
class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CarService carService;

    @Test
    void searchCars_withNoCondition_success() throws Exception { // 검색 조건 X
        // given
        List<CarDto> cars = List.of(
                new CarDto(1L, "아반떼 CN7", "2022", 10000L, 2500L,
                        "image1.jpg", "12가3456", "2023-01-01", "2023-01-01"),
                new CarDto(2L, "소나타 DN8", "2021", 20000L, 3000L,
                        "image2.jpg", "34나5678", "2023-01-01", "2023-01-01")
        );

        given(carService.searchCars(any(CarSearchRequest.class))).willReturn(cars);

        // when & then
        mockMvc.perform(get("/cars"))
                .andDo(print())
                .andExpect(status().isOk())
                // contents 배열 내부의 요소를 검증하도록 수정
                .andExpect(jsonPath("$.contents[0].modelName").value("아반떼 CN7"))
                .andExpect(jsonPath("$.contents[1].modelName").value("소나타 DN8"));
    }

    @Test
    void searchCars_withConditions_success() throws Exception { // 검색 조건 포함
        // given
        String keyword = "아반떼";
        List<CarDto> cars = List.of(
                new CarDto(1L, "아반떼 CN7", "2022", 10000L, 2500L,
                        "image1.jpg", "12가3456", "2023-01-01", "2023-01-01")
        );

        given(carService.searchCars(any(CarSearchRequest.class))).willReturn(cars);

        // when & then
        mockMvc.perform(get("/cars")
                        .param("keyword", keyword)
                        .param("minYear", "2022")
                        .param("maxYear", "2023"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contents[0].modelName").value("아반떼 CN7"));
    }

    @Test
    void getCarDetail_whenCarExists_success() throws Exception { // 차량 상세 정보 조회
        // given
        Long carId = 1L;
        CarDetailDto carDetail = createTestCarDetailDto();

        given(carService.getCarDetail(eq(carId))).willReturn(carDetail);

        // when & then
        mockMvc.perform(get("/cars/{carId}", carId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.car.modelName").value("아반떼 CN7"))  // data -> car
                .andExpect(jsonPath("$.car.year").value("2022"))            // data -> car
                .andExpect(jsonPath("$.car.optionLists.hasNavigation").value(true))  // data -> car
                .andExpect(jsonPath("$.car.accidentHistories").isArray())   // data -> car
                .andExpect(jsonPath("$.car.accidentHistories[0].carPartsPrice").value(500));
    }

    @Test
    void getCarDetail_withNonExistentCar_status404() throws Exception {
        // given
        Long carId = 999L;
        given(carService.getCarDetail(eq(carId)))
                .willThrow(new CarNotFoundException());

        // when & then
        mockMvc.perform(get("/cars/{carId}", carId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("차량을 찾을 수 없습니다."))
                .andExpect(jsonPath("$.code").value("R001"))
                .andExpect(jsonPath("$.status").value(404));
    }


    private CarDetailDto createTestCarDetailDto() {
        return new CarDetailDto(
                1L,                     // carId
                "아반떼 CN7",           // modelName
                "2022",                 // year
                10000L,                 // mileage
                2500L,                  // sellingPrice
                "검정",                 // exteriorColor
                "블랙",                 // interiorColor
                1598L,                  // displacement
                "가솔린",               // fuelType
                "자동8단",              // transmissionType
                "서울 강남구",          // location
                12.5,                   // fuelEfficiency
                "main.jpg",             // mainImage
                2800L,                  // newCarPrice
                "12가3456",             // carNumber
                LocalDateTime.now().toString(),  // createdAt
                LocalDateTime.now().toString(),  // updatedAt
                createTestOptionListDto(),       // optionLists
                List.of(createTestAccidentHistoryDto())  // accidentHistories
        );
    }

    private OptionListDto createTestOptionListDto() {
        return new OptionListDto(
                1L,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                false,
                true,
                false,
                true,
                true,
                true,
                true,
                true,
                true,
                LocalDateTime.now().toString(),
                LocalDateTime.now().toString()
        );
    }

    private AccidentHistoryDto createTestAccidentHistoryDto() {
        return new AccidentHistoryDto(
                1L,
                LocalDateTime.of(2023, 5, 15, 0, 0).toString(),
                500L,
                200L,
                300L,
                LocalDateTime.now().toString(),
                LocalDateTime.now().toString()
        );
    }













}