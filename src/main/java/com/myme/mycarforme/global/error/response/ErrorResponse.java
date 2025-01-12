package com.myme.mycarforme.global.error.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.myme.mycarforme.global.error.ErrorCode;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private String message;
    private String code;
    private int status;
    private List<FieldError> errors;

    // 추가적인 필드 에러를 담기 위한 내부 클래스
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record FieldError(
            String field,
            String value,
            String reason
    ) {}

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode, null);
    }

    public static ErrorResponse of(ErrorCode errorCode, List<FieldError> errors) {
        return new ErrorResponse(errorCode, errors);
    }

    private ErrorResponse(ErrorCode errorCode, List<FieldError> errors) {
        this.message = errorCode.getMessage();
        this.code = errorCode.getCode();
        this.status = errorCode.getStatus();
        this.errors = errors;
    }
}
