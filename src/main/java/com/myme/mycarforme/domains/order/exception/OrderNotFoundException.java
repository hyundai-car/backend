package com.myme.mycarforme.domains.order.exception;

import com.myme.mycarforme.global.error.ErrorCode;
import com.myme.mycarforme.global.error.exception.BusinessException;

public class OrderNotFoundException extends BusinessException {
    public OrderNotFoundException() {
        super(ErrorCode.ORDER_NOT_FOUND);
    }
}
