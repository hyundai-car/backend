package com.myme.mycarforme.domains.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KeycloakErrorDto(
        String error,

        @JsonProperty("error_description")
        String errorDescription
) {
}
