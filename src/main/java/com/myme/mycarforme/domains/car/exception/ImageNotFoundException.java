package com.myme.mycarforme.domains.car.exception;

import com.myme.mycarforme.global.error.ErrorCode;
import com.myme.mycarforme.global.error.exception.BusinessException;

public class ImageNotFoundException extends BusinessException {
  public ImageNotFoundException() {
    super(ErrorCode.IMAGE_NOT_FOUND);
  }
}
