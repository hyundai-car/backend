package com.myme.mycarforme.global.config.keycloak;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KeycloakOidcUserService extends OidcUserService {
    private final KeycloakJwtAuthenticationConverter jwtConverter;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        // 기본 OIDC 사용자 정보 로드
        OidcUser oidcUser = super.loadUser(userRequest);

        // Access Token을 Jwt로 변환
        Jwt jwt = Jwt.withTokenValue(userRequest.getAccessToken().getTokenValue())
                .headers(h -> h.put("alg", "RS256"))
                .claims(c -> c.putAll(extractClaims(userRequest.getAccessToken())))
                .issuedAt(userRequest.getAccessToken().getIssuedAt())
                .expiresAt(userRequest.getAccessToken().getExpiresAt())
                .build();

        // KeycloakJwtAuthenticationConverter를 사용해 권한 변환
        AbstractAuthenticationToken authToken = jwtConverter.convert(jwt);
        if (authToken == null) {
            return oidcUser;
        }

        // 새로운 권한을 가진 OidcUser 반환
        return new DefaultOidcUser(
                authToken.getAuthorities(),
                oidcUser.getIdToken(),
                oidcUser.getUserInfo()
        );
    }

    private Map<String, Object> extractClaims(OAuth2AccessToken accessToken) {
        String[] chunks = accessToken.getTokenValue().split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));

        try {
            return new ObjectMapper().readValue(payload, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse JWT claims", e);
        }
    }
}
