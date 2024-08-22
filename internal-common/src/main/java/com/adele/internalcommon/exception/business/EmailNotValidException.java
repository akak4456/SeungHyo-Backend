package com.adele.internalcommon.exception.business;

import com.adele.internalcommon.response.ErrorCode;

public class EmailNotValidException extends BusinessException{
    public EmailNotValidException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public EmailNotValidException(ErrorCode errorCode) {
        super(errorCode);
    }
}
