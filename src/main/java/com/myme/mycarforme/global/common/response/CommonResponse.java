package com.myme.mycarforme.global.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CommonResponse<T>(T data) {
    public static <T> CommonResponse<T> from(T data) {
        return new CommonResponse<>(data);
    }

    // response가 void인 경우
    public static CommonResponse<Void> empty() {
        return new CommonResponse<>(null);
    }
}
