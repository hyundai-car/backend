package com.myme.mycarforme.domains.auth.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myme.mycarforme.domains.auth.dto.KeycloakErrorDto;
import com.myme.mycarforme.domains.auth.dto.KeycloakTokenDto;
import com.myme.mycarforme.domains.auth.dto.KeycloakUserInfoDto;
import com.myme.mycarforme.domains.auth.exception.KeycloakException;
import com.myme.mycarforme.global.error.ErrorCode;
import com.myme.mycarforme.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class KeycloakClient {

    private final RestTemplate restTemplate;

    @Value("${spring.security.oauth2.client.provider.keycloak.token-uri}")
    private String tokenUri;

    @Value("${spring.security.oauth2.client.provider.keycloak.user-info-uri}")
    private String userInfoUri;

    @Value("${spring.security.oauth2.client.provider.keycloak.issuer-uri}/protocol/openid-connect/logout")
    private String logoutUri;

    private final ObjectMapper objectMapper;

    private final String clientId = "mycarforme-client";

    @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}")
    private String clientSecret;

    private final String redirectUri = "mycarforme://redirect";


    public KeycloakTokenDto getToken(String authorizationCode, String codeVerifier) {
        try {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "authorization_code");
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            params.add("code", authorizationCode);
            params.add("code_verifier", codeVerifier);
            params.add("redirect_uri", redirectUri);

            return executeTokenRequest(params);
        } catch (HttpClientErrorException e) {
            throw handleKeycloakError(e);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.KEYCLOAK_SERVER_ERROR);
        }
    }

    public KeycloakTokenDto reissue(String refreshToken) {
        try {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "refresh_token");
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            params.add("refresh_token", refreshToken);

            return executeTokenRequest(params);
        } catch (HttpClientErrorException e) {
            throw handleKeycloakError(e);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.KEYCLOAK_SERVER_ERROR);
        }
    }

    public boolean logout(String refreshToken) {
        try {
            executeLogoutRequest(refreshToken);
            return true;
        } catch (HttpClientErrorException e) {
            throw handleKeycloakError(e);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.KEYCLOAK_SERVER_ERROR);
        }
    }

    private void executeLogoutRequest(String refreshToken) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("refresh_token", refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        restTemplate.exchange(
                logoutUri,
                HttpMethod.POST,
                request,
                Void.class
        );
    }


    public KeycloakUserInfoDto getUserInfo(String accessToken) {
        try {
            return executeUserInfoRequest(accessToken);
        } catch (HttpClientErrorException e) {
            throw handleKeycloakError(e);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.KEYCLOAK_SERVER_ERROR);
        }
    }

    private KeycloakUserInfoDto executeUserInfoRequest(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<KeycloakUserInfoDto> response = restTemplate.exchange(
                userInfoUri,
                HttpMethod.GET,
                request,
                KeycloakUserInfoDto.class
        );

        return response.getBody();
    }


    private KeycloakTokenDto executeTokenRequest(MultiValueMap<String, String> params) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<KeycloakTokenDto> response = restTemplate.exchange(
                tokenUri,
                HttpMethod.POST,
                request,
                KeycloakTokenDto.class
        );

        return response.getBody();
    }

    private KeycloakException handleKeycloakError(HttpClientErrorException e) {
        try {
            KeycloakErrorDto errorDto = objectMapper.readValue(e.getResponseBodyAsString(), KeycloakErrorDto.class);
            return new KeycloakException(errorDto);
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.KEYCLOAK_SERVER_ERROR);
        }
    }
}
