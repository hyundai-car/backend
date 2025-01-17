package com.myme.mycarforme.domains.car.exception;

import com.myme.mycarforme.global.error.ErrorCode;
import com.myme.mycarforme.global.error.exception.BusinessException;

public class LikeComparisonError extends BusinessException {

    private LikeComparisonError(ErrorCode errorCode) {
        super(errorCode);
    }

    public static LikeComparisonError invalidLength() {
        return new LikeComparisonError(ErrorCode.LIKE_COMPARISON_INVALID_LENGTH);
    }

    public static LikeComparisonError carNotFound() {
        return new LikeComparisonError(ErrorCode.LIKE_COMPARISON_CAR_NOT_FOUND);
    }

    public static LikeComparisonError likeNotFound() {
        return new LikeComparisonError(ErrorCode.LIKE_COMPARISON_LIKE_NOT_FOUND);
    }
}
