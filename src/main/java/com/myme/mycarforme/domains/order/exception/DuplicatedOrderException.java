package com.myme.mycarforme.domains.order.exception;

import com.myme.mycarforme.global.error.ErrorCode;
import com.myme.mycarforme.global.error.exception.BusinessException;

public class DuplicatedOrderException extends BusinessException {
    public DuplicatedOrderException(ErrorCode errorCode) { super(errorCode); }

    public static DuplicatedOrderException forCar() {
        return new DuplicatedOrderException(ErrorCode.DUPLICATED_ORDER_FOUND_FOR_CAR);
    }

    public static DuplicatedOrderException byUser() {
        return new DuplicatedOrderException(ErrorCode.DUPLICATED_ORDER_FOUND_BY_USER);
    }
}
