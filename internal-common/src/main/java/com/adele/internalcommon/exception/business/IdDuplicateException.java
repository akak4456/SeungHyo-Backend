package com.adele.internalcommon.exception.business;


import com.adele.internalcommon.response.ErrorCode;

public class IdDuplicateException extends BusinessException{
    public IdDuplicateException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public IdDuplicateException(ErrorCode errorCode) {
        super(errorCode);
    }
}
