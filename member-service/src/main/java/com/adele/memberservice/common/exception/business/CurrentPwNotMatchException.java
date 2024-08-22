package com.adele.memberservice.common.exception.business;

import com.adele.memberservice.common.ErrorCode;

public class CurrentPwNotMatchException extends BusinessException{
    public CurrentPwNotMatchException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public CurrentPwNotMatchException(ErrorCode errorCode) {
        super(errorCode);
    }
}
