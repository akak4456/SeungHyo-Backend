package com.adele.internalcommon.exception.business.member;


import com.adele.internalcommon.exception.business.BusinessException;
import com.adele.internalcommon.response.ErrorCode;

public class CurrentPwAndNewPwMatchException extends BusinessException {
    public CurrentPwAndNewPwMatchException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public CurrentPwAndNewPwMatchException(ErrorCode errorCode) {
        super(errorCode);
    }
}
