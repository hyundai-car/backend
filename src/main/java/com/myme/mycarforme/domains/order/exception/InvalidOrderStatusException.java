package com.myme.mycarforme.domains.order.exception;

import com.myme.mycarforme.global.error.ErrorCode;
import com.myme.mycarforme.global.error.exception.BusinessException;

public class InvalidOrderStatusException extends BusinessException {
    public InvalidOrderStatusException() {
        super(ErrorCode.INVALID_ORDER_STATUS);
    }
}
