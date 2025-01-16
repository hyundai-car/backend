package com.myme.mycarforme.domains.auth.dto;

public record LoginTokenDto(
        String accessToken,
        String refreshToken,
        Integer expiresIn,
        String scope
) {
    public static LoginTokenDto from(KeycloakTokenDto tokenDto) {
        return new LoginTokenDto(
                tokenDto.accessToken(),
                tokenDto.refreshToken(),
                tokenDto.expiresIn(),
                tokenDto.scope()
        );
    }
}
