package com.myme.mycarforme.domains.auth.api.response;

import com.myme.mycarforme.domains.auth.dto.KeycloakTokenDto;
import com.myme.mycarforme.domains.auth.dto.KeycloakUserInfoDto;
import com.myme.mycarforme.domains.auth.dto.LoginTokenDto;
import com.myme.mycarforme.domains.auth.dto.LoginUserInfoDto;

public record LoginResponse(
        LoginTokenDto token,
        LoginUserInfoDto userInfo
) {
    public static LoginResponse from(KeycloakTokenDto token, KeycloakUserInfoDto userInfo) {
        return new LoginResponse(LoginTokenDto.from(token), LoginUserInfoDto.from(userInfo));
    }
}
