package com.myme.mycarforme.domains.auth.service;

import com.myme.mycarforme.domains.auth.api.response.LoginResponse;
import com.myme.mycarforme.domains.auth.api.response.ReissueResponse;
import com.myme.mycarforme.domains.auth.client.KeycloakClient;
import com.myme.mycarforme.domains.auth.dto.KeycloakTokenDto;
import com.myme.mycarforme.domains.auth.dto.KeycloakUserInfoDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private KeycloakClient keycloakClient;

    @InjectMocks

    private AuthService authService;

    @Test
    void login_withValidCode_success() {
        // given
        String authorizationCode = "test_auth_code";
        String codeVerifier = "test_code_verifier";
        KeycloakTokenDto expectedToken = new KeycloakTokenDto(
                "test_access_token",
                "test_refresh_token",
                "Bearer",
                3600,
                "openid profile email"
        );

        KeycloakUserInfoDto expectedUser = new KeycloakUserInfoDto(
                "test@example.com",
                "Test user",
                "010-1234-5678"
        );

        when(keycloakClient.getToken(authorizationCode, codeVerifier))
                .thenReturn(expectedToken);

        when(keycloakClient.getUserInfo(expectedToken.accessToken()))
                .thenReturn(expectedUser);

        // when
        LoginResponse result = authService.login(authorizationCode, codeVerifier);

        // then
        assertThat(result).isNotNull();
        assertThat(result.token().accessToken()).isEqualTo(expectedToken.accessToken());
        assertThat(result.token().refreshToken()).isEqualTo(expectedToken.refreshToken());
        assertThat(result.userInfo())
                        .extracting("email", "name", "phoneNumber")
                        .containsExactly(expectedUser.email(), expectedUser.name(), expectedUser.phoneNumber());
        verify(keycloakClient).getToken(authorizationCode, codeVerifier);
    }

    @Test
    void reissue_whenValidToken_thenReturnNewToken() {
        // given
        String refreshToken = "test_refresh_token";
        KeycloakTokenDto expectedToken = new KeycloakTokenDto(
                "new_access_token",
                "new_refresh_token",
                "Bearer",
                3600,
                "openid profile email"
        );

        when(keycloakClient.reissue(refreshToken))
                .thenReturn(expectedToken);

        // when
        ReissueResponse result = authService.reissue(refreshToken);

        // then
        assertThat(result).isNotNull();
        assertThat(result.accessToken()).isEqualTo(expectedToken.accessToken());
        assertThat(result.refreshToken()).isEqualTo(expectedToken.refreshToken());
        verify(keycloakClient).reissue(refreshToken);
    }

    @Test
    void logout_withValidToken_thenSuccess() {
        // given
        String refreshToken = "test_refresh_token";
        when(keycloakClient.logout(refreshToken)).thenReturn(true);

        // when
        boolean result = authService.logout(refreshToken);

        // then
        assertThat(result).isTrue();
        verify(keycloakClient).logout(refreshToken);
    }
}