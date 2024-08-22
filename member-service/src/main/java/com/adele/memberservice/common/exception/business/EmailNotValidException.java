package com.adele.memberservice.common.exception.business;

import com.adele.memberservice.common.ErrorCode;

public class EmailNotValidException extends BusinessException{
    public EmailNotValidException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public EmailNotValidException(ErrorCode errorCode) {
        super(errorCode);
    }
}
