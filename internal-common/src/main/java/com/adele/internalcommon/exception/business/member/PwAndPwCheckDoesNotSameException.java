package com.adele.internalcommon.exception.business.member;


import com.adele.internalcommon.exception.business.BusinessException;
import com.adele.internalcommon.response.ErrorCode;

public class PwAndPwCheckDoesNotSameException extends BusinessException {
    public PwAndPwCheckDoesNotSameException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public PwAndPwCheckDoesNotSameException(ErrorCode errorCode) {
        super(errorCode);
    }
}
