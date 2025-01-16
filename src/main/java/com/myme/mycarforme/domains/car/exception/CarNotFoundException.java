package com.myme.mycarforme.domains.car.exception;

import com.myme.mycarforme.global.error.ErrorCode;
import com.myme.mycarforme.global.error.exception.BusinessException;
import lombok.Getter;

@Getter
public class CarNotFoundException extends BusinessException {
    public CarNotFoundException() {
        super(ErrorCode.CAR_NOT_FOUND);
    }
}
