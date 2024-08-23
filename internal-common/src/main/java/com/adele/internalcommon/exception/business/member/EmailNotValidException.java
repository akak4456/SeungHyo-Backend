package com.adele.internalcommon.exception.business.member;

import com.adele.internalcommon.exception.business.BusinessException;
import com.adele.internalcommon.response.ErrorCode;

public class EmailNotValidException extends BusinessException {
    public EmailNotValidException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public EmailNotValidException(ErrorCode errorCode) {
        super(errorCode);
    }
}
