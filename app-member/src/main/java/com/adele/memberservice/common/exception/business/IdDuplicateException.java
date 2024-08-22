package com.adele.memberservice.common.exception.business;

import com.adele.memberservice.common.ErrorCode;

public class IdDuplicateException extends BusinessException{
    public IdDuplicateException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public IdDuplicateException(ErrorCode errorCode) {
        super(errorCode);
    }
}
