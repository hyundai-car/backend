package com.myme.mycarforme.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(400, "C001", "요청 파라미터가 유효하지 않습니다."),
    INVALID_TYPE_VALUE(400, "C002", "요청 파라미터 타입이 유효하지 않습니다."),
    MISSING_INPUT_VALUE(400, "C003", "필수 파라미터가 누락되었습니다."),
    METHOD_NOT_ALLOWED(405, "C004", "HTTP 메소드가 유효하지 않습니다."),
    ROUTE_NOT_FOUND(404, "C005", "요청 경로를 찾을 수 없습니다."),
    ILLEGAL_ARGUMENT(400, "C006", "내부 파라미터가 유효하지 않습니다."),
    INTERNAL_SERVER_ERROR(500, "C100", "서버 에러."),

    // Auth
    INVALID_TOKEN(401, "A001", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(401, "A002", "만료된 토큰입니다."),
    INVALID_GRANT(400, "A003", "유효하지 않은 인증 정보입니다."),
    KEYCLOAK_SERVER_ERROR(500, "A004", "인증 서버 에러가 발생했습니다."),

    // User
    USER_NOT_FOUND(404, "U001", "유저를 찾을 수 없습니다."),
    DUPLICATE_USER(409, "U002", "유저가 이미 존재합니다."),

    // Car
    CAR_NOT_FOUND(404, "R001", "차량을 찾을 수 없습니다."),
    IMAGE_NOT_FOUND(404,"R002","이미지를 찾을 수 없습니다."),

    // Like
    LIKE_COMPARISON_INVALID_LENGTH(400, "L001", "비교할 차량이 2개 미만입니다."),
    LIKE_COMPARISON_CAR_NOT_FOUND(404, "L002", "비교할 차량을 찾을 수 없습니다."),
    LIKE_COMPARISON_LIKE_NOT_FOUND(404, "L003", "비교할 차랑이 찜 목록에 없습니다.");


    // ...

    private final int status;
    private final String code;
    private final String message;
}
