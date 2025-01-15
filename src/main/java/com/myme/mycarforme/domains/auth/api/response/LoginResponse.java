package com.myme.mycarforme.domains.auth.api.response;

import com.myme.mycarforme.domains.auth.dto.KeycloakTokenDto;
import com.myme.mycarforme.domains.auth.dto.KeycloakUserInfoDto;

public record LoginResponse(
        KeycloakTokenDto token,
        KeycloakUserInfoDto userInfo
) {
    public static LoginResponse of(KeycloakTokenDto token, KeycloakUserInfoDto userInfo) {
        return new LoginResponse(token, userInfo);
    }
}
