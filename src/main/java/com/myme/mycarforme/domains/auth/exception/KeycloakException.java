package com.myme.mycarforme.domains.auth.exception;

import com.myme.mycarforme.domains.auth.dto.KeycloakErrorDto;
import com.myme.mycarforme.global.error.ErrorCode;
import com.myme.mycarforme.global.error.exception.BusinessException;

public class KeycloakException extends BusinessException {
    public KeycloakException(KeycloakErrorDto errorDto) {
        super(convertToErrorCode(errorDto));
    }

    private static ErrorCode convertToErrorCode(KeycloakErrorDto errorDto) {
        return switch (errorDto.error()) {
            case "invalid_token" -> ErrorCode.INVALID_TOKEN;
            case "invalid_grant" -> ErrorCode.INVALID_GRANT;
            case "expired_token" -> ErrorCode.EXPIRED_TOKEN;
            default -> ErrorCode.KEYCLOAK_SERVER_ERROR;
        };
    }
}
