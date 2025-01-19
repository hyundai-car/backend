package com.myme.mycarforme.domains.order.exception;

import com.myme.mycarforme.global.error.ErrorCode;
import com.myme.mycarforme.global.error.exception.BusinessException;

public class UserTrackingCodeNotFoundException extends BusinessException {
    public UserTrackingCodeNotFoundException() {
        super(ErrorCode.USER_TRACKING_CODE_NOT_FOUND);
    }
}
