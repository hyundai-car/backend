package com.myme.mycarforme.domains.car.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.myme.mycarforme.domains.car.api.response.LikeResponse;
import com.myme.mycarforme.domains.car.exception.CarNotFoundException;
import com.myme.mycarforme.domains.car.service.LikeService;
import com.myme.mycarforme.global.util.security.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @BeforeEach
    void setUp() { objectMapper = new ObjectMapper(); }

    @Test
    void toggleLike_withValidCarId_thenSuccess() throws Exception {
        // Given
        LikeResponse likeResponse = new LikeResponse(TEST_CAR_ID, true);

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
        LikeResponse likeResponse = new LikeResponse(TEST_CAR_ID, true);

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
}