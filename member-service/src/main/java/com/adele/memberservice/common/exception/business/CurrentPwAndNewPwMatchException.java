package com.adele.memberservice.common.exception.business;

import com.adele.memberservice.common.ErrorCode;

public class CurrentPwAndNewPwMatchException extends BusinessException{
    public CurrentPwAndNewPwMatchException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public CurrentPwAndNewPwMatchException(ErrorCode errorCode) {
        super(errorCode);
    }
}
