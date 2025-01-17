package com.myme.mycarforme.domains.auth.dto;

import lombok.Builder;

@Builder
public record LoginUserInfoDto(
        String email,
        String name,
        String phoneNumber
) {
    public static LoginUserInfoDto from(KeycloakUserInfoDto userInfoDto) {
        return new LoginUserInfoDto(
                userInfoDto.email(), userInfoDto.name(), userInfoDto.phoneNumber()
        );
    }
}
