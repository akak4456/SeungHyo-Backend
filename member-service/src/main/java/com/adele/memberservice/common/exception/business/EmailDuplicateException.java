package com.adele.memberservice.common.exception.business;

import com.adele.memberservice.common.ErrorCode;

public class EmailDuplicateException extends BusinessException{
    public EmailDuplicateException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public EmailDuplicateException(ErrorCode errorCode) {
        super(errorCode);
    }
}
