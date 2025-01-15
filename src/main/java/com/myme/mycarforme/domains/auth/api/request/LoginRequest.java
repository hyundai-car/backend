package com.myme.mycarforme.domains.auth.api.request;

public record LoginRequest(
        String authorizationCode,
        String codeVerifier
) {
}
