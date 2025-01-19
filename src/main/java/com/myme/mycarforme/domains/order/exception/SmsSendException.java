package com.myme.mycarforme.domains.order.exception;

import com.myme.mycarforme.global.error.ErrorCode;
import com.myme.mycarforme.global.error.exception.BusinessException;

public class SmsSendException extends BusinessException {
    public SmsSendException() {
        super(ErrorCode.SMS_SEND_ERROR);
    }
}
