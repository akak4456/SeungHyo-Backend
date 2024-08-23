package com.adele.internalcommon.exception.business.member;


import com.adele.internalcommon.exception.business.BusinessException;
import com.adele.internalcommon.response.ErrorCode;

public class NewPwAndNewPwCheckDoesNotMatchException extends BusinessException {
    public NewPwAndNewPwCheckDoesNotMatchException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public NewPwAndNewPwCheckDoesNotMatchException(ErrorCode errorCode) {
        super(errorCode);
    }
}
