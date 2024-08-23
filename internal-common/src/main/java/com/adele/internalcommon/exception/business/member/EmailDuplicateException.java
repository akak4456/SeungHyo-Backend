package com.adele.internalcommon.exception.business.member;


import com.adele.internalcommon.exception.business.BusinessException;
import com.adele.internalcommon.response.ErrorCode;

public class EmailDuplicateException extends BusinessException {
    public EmailDuplicateException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public EmailDuplicateException(ErrorCode errorCode) {
        super(errorCode);
    }
}
