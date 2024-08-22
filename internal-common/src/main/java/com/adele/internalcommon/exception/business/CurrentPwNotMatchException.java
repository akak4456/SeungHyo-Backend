package com.adele.internalcommon.exception.business;


import com.adele.internalcommon.response.ErrorCode;

public class CurrentPwNotMatchException extends BusinessException{
    public CurrentPwNotMatchException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public CurrentPwNotMatchException(ErrorCode errorCode) {
        super(errorCode);
    }
}
