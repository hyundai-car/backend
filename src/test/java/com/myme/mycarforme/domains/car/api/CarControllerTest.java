package com.myme.mycarforme.domains.car.api;

import com.myme.mycarforme.domains.car.api.request.CarSearchRequest;
import com.myme.mycarforme.domains.car.api.response.MmScoreResponse;
import com.myme.mycarforme.domains.car.dto.*;
import com.myme.mycarforme.domains.car.exception.CarNotFoundException;
import com.myme.mycarforme.domains.car.exception.ImageNotFoundException;
import com.myme.mycarforme.domains.car.service.CarService;
import com.myme.mycarforme.global.util.helper.CalculateHelper.CarScoreNormalizer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.when;
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
                new CarDto(1L, "아반떼 CN7", "23년 2월",10000L, 2500L,
                        "image1.jpg", "12가3456", true, 1L,"2023-01-01","2023-01-01"),
                new CarDto(2L, "소나타 DN8", "23년 5월", 20000L, 3000L,
                        "image2.jpg", "34나5678",  true, 2L,"2023-01-01", "2023-01-01")
        );

        PageImpl<CarDto> carPage = new PageImpl<>(cars,
                PageRequest.of(0, 10),
                cars.size());

        given(carService.searchCars(
                any(CarSearchRequest.class),
                anyString(),
                any(Pageable.class))
        ).willReturn(carPage);

        // when & then
        mockMvc.perform(get("/api/cars")
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contents[0].carName").value("아반떼 CN7"))
                .andExpect(jsonPath("$.contents[1].carName").value("소나타 DN8"))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.pageNumber").value(0));
    }

    @Test
    void searchCars_withConditions_success() throws Exception { // 검색 조건 포함
        // given
        String keyword = "아반떼";
        List<CarDto> cars = List.of(
                new CarDto(1L, "아반떼 CN7", "23년 2월",10000L, 2500L,
                        "image1.jpg", "12가3456", true, 1L,"2023-01-01","2023-01-01")
        );

        PageImpl<CarDto> carPage = new PageImpl<>(cars,
                PageRequest.of(0, 10),
                cars.size());

        given(carService.searchCars(
                any(CarSearchRequest.class),
                anyString(),
                any(Pageable.class))
        ).willReturn(carPage);

        // when & then
        mockMvc.perform(get("/api/cars")
                        .param("keyword", keyword)
                        .param("minYear", "2022")
                        .param("maxYear", "2023"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contents[0].carName").value("아반떼 CN7"));
    }

    @Test
    void getCarDetail_whenCarExists_success() throws Exception { // 차량 상세 정보 조회
        // given
        Long carId = 1L;
        CarDetailDto carDetail = createTestCarDetailDto();

        when(carService.getCarDetail(eq(carId))).thenReturn(carDetail);

        // when & then
        mockMvc.perform(get("/api/cars/{carId}", carId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.car.carName").value("아반떼 CN7"))  // data -> car
                .andExpect(jsonPath("$.car.initialRegistration").value("22년 01월"))            // data -> car
                .andExpect(jsonPath("$.car.optionLists.hasNavigation").value(true))  // data -> car
                .andExpect(jsonPath("$.car.accidentHistoryList").isArray())   // data -> car
                .andExpect(jsonPath("$.car.accidentHistoryList[0].carPartsPrice").value(500));
    }

    @Test
    void getCarDetail_withNonExistentCar_status404() throws Exception { // 차가 존재하지 않음
        // given
        Long carId = 999L;
        given(carService.getCarDetail(eq(carId)))
                .willThrow(new CarNotFoundException());

        // when & then
        mockMvc.perform(get("/api/cars/{carId}", carId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("차량을 찾을 수 없습니다."))
                .andExpect(jsonPath("$.code").value("R001"))
                .andExpect(jsonPath("$.status").value(404));
    }


    private CarDetailDto createTestCarDetailDto() {
        return  new CarDetailDto(
                1L,                     // carId
                "아반떼 CN7",           // carName
                "22년 01월",             // initialRegistration (String으로 변경)
                10000L,                // mileage
                2500L,                 // sellingPrice
                95.5,                  // mmScore 추가
                "검정",                // exteriorColor
                "블랙",                // interiorColor
                1598L,                 // displacement
                "가솔린",              // fuelType
                "자동8단",             // transmissionType
                "서울 강남구",         // location
                85.4,
                "main.jpg",            // mainImage
                2800L,                 // newCarPrice
                "12가3456",            // carNumber
                5L,                    // seating
                LocalDateTime.now().toString(),   // createdAt
                LocalDateTime.now().toString(),   // updatedAt
                createTestOptionListDto(),        // optionLists
                List.of(createTestAccidentHistoryDto()), // accidentHistoryList
                1,                      // accidentCount 추가,
                ComparisonGraphItemDto.builder()
                        .mmScoreNorm(95.5)
                        .accidentCountNorm(CarScoreNormalizer.normalizeAccidentCount(1))
                        .initialRegistrationNorm(CarScoreNormalizer.normalizeInitialRegistration("2022.01.01"))
                        .mileageNorm(CarScoreNormalizer.normalizeMileage(10000L))
                        .fuelEfficiencyNorm(CarScoreNormalizer.normalizeFuelEfficiency(15.0))
                        .build()
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

    @Test
    void get360Images_whenImagesExist_success() throws Exception {
        // given
        Long carId = 1L;
        List<Exterior360ImageDto> images = List.of(
                new Exterior360ImageDto(1L, "exterior_0.jpg", 0, "2023-01-01", "2023-01-01"),
                new Exterior360ImageDto(2L, "exterior_10.jpg", 10, "2023-01-01", "2023-01-01")
        );

        given(carService.get360Images(eq(carId))).willReturn(images);

        // when & then
        mockMvc.perform(get("/api/cars/{carId}/360images", carId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contents[0].imageUrl").value("exterior_0.jpg"))
                .andExpect(jsonPath("$.contents[0].rotationDegree").value(0))
                .andExpect(jsonPath("$.contents[1].imageUrl").value("exterior_10.jpg"))
                .andExpect(jsonPath("$.contents[1].rotationDegree").value(10));
    }

    @Test
    void get360Images_whenImagesNotFound_status404() throws Exception {
        // given
        Long carId = 999L;
        given(carService.get360Images(eq(carId)))
                .willThrow(new ImageNotFoundException());

        // when & then
        mockMvc.perform(get("/api/cars/{carId}/360images", carId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("이미지를 찾을 수 없습니다."));
    }

    @Test
    void getDetailImages_whenImagesExist_success() throws Exception {
        // given
        Long carId = 1L;
        List<DetailImageDto> images = List.of(
                new DetailImageDto(1L, "detail_1.jpg", "2023-01-01", "2023-01-01"),
                new DetailImageDto(2L, "detail_2.jpg", "2023-01-01", "2023-01-01")
        );

        given(carService.getDetailImages(eq(carId))).willReturn(images);

        // when & then
        mockMvc.perform(get("/api/cars/{carId}/detailimages", carId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contents[0].imageUrl").value("detail_1.jpg"))
                .andExpect(jsonPath("$.contents[1].imageUrl").value("detail_2.jpg"));
    }

    @Test
    void getDetailImages_whenImagesNotFound_status404() throws Exception {
        // given
        Long carId = 999L;
        given(carService.getDetailImages(eq(carId)))
                .willThrow(new ImageNotFoundException());

        // when & then
        mockMvc.perform(get("/api/cars/{carId}/detailimages", carId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("이미지를 찾을 수 없습니다."));
    }

    @Test
    void getMmscores_success() throws Exception {
        // given
        List<MmScoreDto> mmScoreDtos = List.of(
                new MmScoreDto(
                        1L, "제네시스 G80", "2023년 3월", 1000L, 50000000L,
                        "genesis_g80.jpg", 98.5,
                        true, 0L, "2023-01-01", "2023-01-01"
                ),
                new MmScoreDto(
                        2L, "그랜저 하이브리드", "2022년 2월", 5000L, 45000000L,
                        "grandeur.jpg", 95.0,
                        false, 0L, "2023-01-01", "2023-01-01"
                )
        );
        MmScoreResponse response = MmScoreResponse.from(mmScoreDtos);
        given(carService.getTop5CarsByMmScore(anyString())).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/cars/mmscores"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contents[0].carName").value("제네시스 G80"))
                .andExpect(jsonPath("$.contents[0].mmScore").value(98.5))
                .andExpect(jsonPath("$.contents[1].carName").value("그랜저 하이브리드"))
                .andExpect(jsonPath("$.contents[1].mmScore").value(95.0));
    }

    @Test
    void getMmscores_whenNoData_returnEmptyList() throws Exception {
        // given
        given(carService.getTop5CarsByMmScore(anyString()))
                .willReturn(MmScoreResponse.from(List.of()));

        // when & then
        mockMvc.perform(get("/api/cars/mmscores"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contents").isEmpty());
    }













}