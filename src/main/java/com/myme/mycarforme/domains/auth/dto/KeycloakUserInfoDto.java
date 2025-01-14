package com.myme.mycarforme.domains.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KeycloakUserInfoDto(
        String email,

        @JsonProperty("given_name")
        String name,

        @JsonProperty("phone_number")
        String phoneNumber
) {
}
