package com.adele.memberservice.common.exception.business;

import com.adele.memberservice.common.ErrorCode;

public class EmailCheckCodeNotCorrectException extends BusinessException{
    public EmailCheckCodeNotCorrectException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public EmailCheckCodeNotCorrectException(ErrorCode errorCode) {
        super(errorCode);
    }
}
