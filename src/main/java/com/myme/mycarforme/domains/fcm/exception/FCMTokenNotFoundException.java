package com.myme.mycarforme.domains.fcm.exception;

import com.myme.mycarforme.global.error.ErrorCode;
import com.myme.mycarforme.global.error.exception.BusinessException;

public class FCMTokenNotFoundException extends BusinessException {

    public FCMTokenNotFoundException() {
        super(ErrorCode.FCM_TOKEN_NOT_FOUND);
    }
}
