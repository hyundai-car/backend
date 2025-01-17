package com.myme.mycarforme.domains.order.exception;

import com.myme.mycarforme.global.error.ErrorCode;
import com.myme.mycarforme.global.error.exception.BusinessException;

public class DuplicatedOrderException extends BusinessException {
    public DuplicatedOrderException() {
        super(ErrorCode.DUPLICATED_ORDER_FOUND);
    }
}
