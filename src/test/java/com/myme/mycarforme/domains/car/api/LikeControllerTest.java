package com.myme.mycarforme.domains.car.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.myme.mycarforme.domains.car.api.request.LikeComparisonRequest;
import com.myme.mycarforme.domains.car.api.response.LikeCarListResponse;
import com.myme.mycarforme.domains.car.api.response.LikeComparisonResponse;
import com.myme.mycarforme.domains.car.api.response.LikeResponse;
import com.myme.mycarforme.domains.car.domain.Car;
import com.myme.mycarforme.domains.car.domain.Like;
import com.myme.mycarforme.domains.car.domain.OptionList;
import com.myme.mycarforme.domains.car.exception.CarNotFoundException;
import com.myme.mycarforme.domains.car.exception.LikeComparisonError;
import com.myme.mycarforme.domains.car.service.LikeService;
import com.myme.mycarforme.global.util.security.SecurityUtil;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = LikeController.class)
@AutoConfigureMockMvc(addFilters = false)
class LikeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LikeService likeService;

    private ObjectMapper objectMapper;

    private final String TEST_USER_ID = "test-user-id";
    private final Long TEST_CAR_ID = 1L;

    private final String TEST_CREATED_AT = "test-created-at";
    private final String TEST_UPDATED_AT = "test-updated-at";

    private Car TEST_CAR;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        TEST_CAR = Car.builder()
                .id(TEST_CAR_ID) // ID를 포함
                .carName("Test car")
                .carType("luxury")
                .year(2022L)
                .initialRegistration("2022.04.11")
                .mileage(31200L)
                .driveType("Auto")
                .displacement(1999L)
                .sellingPrice(4800L)
                .exteriorColor("color")
                .interiorColor("color")
                .seating(5L)
                .fuelType("gasoline")
                .transmissionType("2WD")
                .isOnSale(1)
                .location("location")
                .mmScore(90.11)
                .mainImage("https://image")
                .newCarPrice(6000L)
                .savingAccount(1200.0)
                .carNumber("123가1234")
                .accidentSeverity(0.0)
                .repairProbability(30.4)
                .predictedPrice(5000.0)
                .cityEfficiency(9.8)
                .highwayEfficiency(11.0)
                .paymentDeliveryStatus(0)
                .optionList(OptionList.builder().build())
                .build();

        LocalDateTime now = LocalDateTime.now();
        ReflectionTestUtils.setField(TEST_CAR, "createdAt", now);
        ReflectionTestUtils.setField(TEST_CAR, "updatedAt", now);
    }

    @Test
    void toggleLike_withValidCarId_thenSuccess() throws Exception {
        // Given
        LikeResponse likeResponse = new LikeResponse(TEST_CAR_ID, true, TEST_CREATED_AT, TEST_UPDATED_AT);

        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            mockedStatic.when(SecurityUtil::getUserId).thenReturn(TEST_USER_ID);
            when(likeService.toggleLike(TEST_USER_ID, TEST_CAR_ID)).thenReturn(likeResponse);

            // When & Then
            mockMvc.perform(post("/api/likes/{carId}", TEST_CAR_ID)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.carId").value(TEST_CAR_ID))
                    .andExpect(jsonPath("$.isLike").value(true));
        }
    }

    @Test
    void toggleLike_withInvalidCarId_throwException() throws Exception {
        // Given
        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            mockedStatic.when(SecurityUtil::getUserId).thenReturn(TEST_USER_ID);
            when(likeService.toggleLike(TEST_USER_ID, TEST_CAR_ID))
                    .thenThrow(new CarNotFoundException());

            // When & Then
            mockMvc.perform(post("/api/likes/{carId}", TEST_CAR_ID)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value("R001"));
        }
    }

    @Test
    void getLikeByCarId_withValidCarId_thenSuccess() throws Exception {
        // Given
        LikeResponse likeResponse = new LikeResponse(TEST_CAR_ID, true, TEST_CREATED_AT, TEST_UPDATED_AT);

        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            mockedStatic.when(SecurityUtil::getUserId).thenReturn(TEST_USER_ID);
            when(likeService.getLikeByCarId(TEST_USER_ID, TEST_CAR_ID)).thenReturn(likeResponse);

            // When & Then
            mockMvc.perform(get("/api/likes/{carId}", TEST_CAR_ID)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.carId").value(TEST_CAR_ID))
                    .andExpect(jsonPath("$.isLike").value(true));
        }
    }

    @Test
    void getLikeByCarId_withInvalidCarId_throwException() throws Exception {
        // Given
        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            mockedStatic.when(SecurityUtil::getUserId).thenReturn(TEST_USER_ID);
            when(likeService.getLikeByCarId(TEST_USER_ID, TEST_CAR_ID))
                    .thenThrow(new CarNotFoundException());

            // When & Then
            mockMvc.perform(get("/api/likes/{carId}", TEST_CAR_ID)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value("R001"));
        }
    }

    @Test
    void getLikeCarList_withValidPageable_thenSuccess() throws Exception {
        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            // Given
            String page = "0";
            String size = "10";
            String sort = "createdAt,desc";

            Pageable pageable = PageRequest.of(
                    Integer.parseInt(page),
                    Integer.parseInt(size),
                    Sort.by(Sort.Direction.DESC, "createdAt")
            );

            Like like = Like.builder()
                    .car(TEST_CAR)
                    .userId(TEST_USER_ID)
                    .isLike(true)
                    .build();

            LocalDateTime now = LocalDateTime.now();
            ReflectionTestUtils.setField(like, "createdAt", now);
            ReflectionTestUtils.setField(like, "updatedAt", now);

            PageImpl<Like> carPage = new PageImpl<>(List.of(like), pageable, 1);
            LikeCarListResponse likeCarListResponse = LikeCarListResponse.from(carPage);

            mockedStatic.when(SecurityUtil::getUserId).thenReturn(TEST_USER_ID);
            when(likeService.getLikeCarList(TEST_USER_ID, pageable))
                    .thenReturn(likeCarListResponse);

            // When & Then
            mockMvc.perform(get("/api/likes")
                            .param("page", page)
                            .param("size", size)
                            .param("sort", sort)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.contents").isArray())
                    .andExpect(jsonPath("$.contents[0].carId").value(1L))
                    .andExpect(jsonPath("$.contents[0].carName").value("Test car"))
                    .andExpect(jsonPath("$.pageNumber").value(0))
                    .andExpect(jsonPath("$.totalElements").value(1))
                    .andDo(print());
        }
    }

    @Test
    void getLikeComparison_withValidCarIdList_thenSuccess() throws Exception {
        // Given
        Set<Long> carIdList = Set.of(1L, 2L);
        LikeComparisonRequest request = new LikeComparisonRequest(carIdList);

        LikeComparisonResponse response = LikeComparisonResponse.of(
                TEST_CAR,
                90.11,  // mmScoreAvg
                1.0,    // accidentCountAvg
                "2022.04.11", // initialRegistrationAvg
                31200.0,  // mileageAvg
                10.4,     // fuelEfficiencyAvg
                95.0,     // bestMmScoreNorm
                90.0,     // bestAccidentCountNorm
                85.0,     // bestInitialRegistrationNorm
                80.0,     // bestMileageNorm
                75.0,     // bestFuelEfficiencyNorm
                85.0,     // avgMmScoreNorm
                80.0,     // avgAccidentCountNorm
                75.0,     // avgInitialRegistrationNorm
                70.0,     // avgMileageNorm
                65.0,     // avgFuelEfficiencyNorm
                List.of(2L)  // otherCarIds
        );

        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            mockedStatic.when(SecurityUtil::getUserId).thenReturn(TEST_USER_ID);
            when(likeService.getComparisonLikeCarList(TEST_USER_ID, carIdList)).thenReturn(response);

            // When & Then
            mockMvc.perform(post("/api/likes/comparisons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.bestCar.carId").value(TEST_CAR_ID))
                    .andExpect(jsonPath("$.comparisons.mmScoreAvg").value(90.11))
                    .andExpect(jsonPath("$.comparisons.accidentCountAvg").value(1.0))
                    .andExpect(jsonPath("$.comparisons.initialRegistrationAvg").value("22년 04월"))
                    .andExpect(jsonPath("$.graph.best.mmScoreNorm").value(95.0))
                    .andExpect(jsonPath("$.graph.avg.mmScoreNorm").value(85.0))
                    .andExpect(jsonPath("$.otherCarIds[0]").value(2L))
                    .andDo(print());
        }
    }

    @Test
    void getLikeComparison_withInvalidCarIdList_throwException() throws Exception {
        // Given
        Set<Long> carIdList = Set.of(1L);  // 2개 미만의 차량
        LikeComparisonRequest request = new LikeComparisonRequest(carIdList);

        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            mockedStatic.when(SecurityUtil::getUserId).thenReturn(TEST_USER_ID);
            when(likeService.getComparisonLikeCarList(TEST_USER_ID, carIdList))
                    .thenThrow(LikeComparisonError.invalidLength());

            // When & Then
            mockMvc.perform(post("/api/likes/comparisons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value("L001"))
                    .andExpect(jsonPath("$.message").value("비교할 차량이 2개 미만입니다."))
                    .andDo(print());
        }
    }

}