package com.adele.internalcommon.exception.business;


import com.adele.internalcommon.response.ErrorCode;

public class EmailCheckCodeNotCorrectException extends BusinessException{
    public EmailCheckCodeNotCorrectException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public EmailCheckCodeNotCorrectException(ErrorCode errorCode) {
        super(errorCode);
    }
}
