package com.myme.mycarforme.domains.auth.api.response;

import com.myme.mycarforme.domains.auth.dto.KeycloakTokenDto;

public record ReissueResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Integer expiresIn
) {
    public static ReissueResponse from(final KeycloakTokenDto token) {
        return new ReissueResponse(token.accessToken(), token.refreshToken(), token.tokenType(), token.expiresIn());
    }
}
