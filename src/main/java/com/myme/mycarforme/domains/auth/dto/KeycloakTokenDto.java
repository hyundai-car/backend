package com.myme.mycarforme.domains.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KeycloakTokenDto(
        @JsonProperty("access_token")
        String accessToken,

        @JsonProperty("refresh_token")
        String refreshToken,

        @JsonProperty("token_type")
        String tokenType,

        @JsonProperty("expires_in")
        Integer expiresIn,

        @JsonProperty("scope")
        String scope
) {
}
