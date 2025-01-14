package com.myme.mycarforme.domains.auth.service;

import com.myme.mycarforme.domains.auth.api.response.LoginResponse;
import com.myme.mycarforme.domains.auth.client.KeycloakClient;
import com.myme.mycarforme.domains.auth.dto.KeycloakTokenDto;
import com.myme.mycarforme.domains.auth.dto.KeycloakUserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KeycloakClient keycloakClient;

    public LoginResponse login(String authorizationCode, String codeVerifier) {
        KeycloakTokenDto token = keycloakClient.getToken(authorizationCode, codeVerifier);

        KeycloakUserInfoDto user = keycloakClient.getUserInfo(token.accessToken());

        return LoginResponse.of(token, user);
    }

    public KeycloakTokenDto reissue(String refreshToken) {
        return keycloakClient.reissue(refreshToken);
    }

    public boolean logout(String refreshToken) {
        return keycloakClient.logout(refreshToken);
    }
}
