package com.myme.mycarforme.domains.auth.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myme.mycarforme.domains.auth.api.request.LoginRequest;
import com.myme.mycarforme.domains.auth.api.request.ReissueRequest;
import com.myme.mycarforme.domains.auth.api.response.LoginResponse;
import com.myme.mycarforme.domains.auth.api.response.ReissueResponse;
import com.myme.mycarforme.domains.auth.dto.KeycloakTokenDto;
import com.myme.mycarforme.domains.auth.dto.KeycloakUserInfoDto;
import com.myme.mycarforme.domains.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void login_withValidData_thenSuccess() throws Exception {
        // given
        KeycloakTokenDto tokenDto = new KeycloakTokenDto(
                "access_token",
                "refresh_token",
                "Bearer",
                3600,
                "openid profile email"
        );

        KeycloakUserInfoDto userInfoDto = new KeycloakUserInfoDto(
                "test@example.com",
                "Test user",
                "010-1234-5678"
        );

        LoginResponse loginResponse = LoginResponse.from(tokenDto, userInfoDto);
        LoginRequest loginRequest = new LoginRequest("test_auth_code", "test_code_verifier");

        when(authService.login(anyString(), anyString())).thenReturn(loginResponse);

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token.accessToken").value(tokenDto.accessToken()))
                .andExpect(jsonPath("$.token.refreshToken").value(tokenDto.refreshToken()))
                .andExpect(jsonPath("$.userInfo.email").value(userInfoDto.email()))
                .andExpect(jsonPath("$.userInfo.name").value(userInfoDto.name()));
    }

    @Test
    void reissue_whenValidData_thenReturnToken() throws Exception {
        // given
        KeycloakTokenDto tokenDto = new KeycloakTokenDto(
                "new_access_token",
                "new_refresh_token",
                "Bearer",
                3600,
                "openid profile email"
        );
        when(authService.reissue(anyString())).thenReturn(ReissueResponse.from(tokenDto));

        // Create request body
        ReissueRequest request = new ReissueRequest("test_refresh_token");
        String requestJson = new ObjectMapper().writeValueAsString(request);

        // when & then
        mockMvc.perform(post("/api/auth/reissue")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(tokenDto.accessToken()))
                .andExpect(jsonPath("$.refreshToken").value(tokenDto.refreshToken()))
                .andExpect(jsonPath("$.tokenType").value(tokenDto.tokenType()));
    }

    @Test
    void logout_withValidToken_thenSuccess() throws Exception {
        // given
        when(authService.logout(anyString())).thenReturn(true);

        // when & then
        mockMvc.perform(post("/api/auth/logout")
                        .param("refresh_token", "test_refresh_token")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andDo(print())
                .andExpect(status().isOk());
    }
}