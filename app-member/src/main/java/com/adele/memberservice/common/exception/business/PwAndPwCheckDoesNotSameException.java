package com.adele.memberservice.common.exception.business;

import com.adele.memberservice.common.ErrorCode;

public class PwAndPwCheckDoesNotSameException extends BusinessException{
    public PwAndPwCheckDoesNotSameException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public PwAndPwCheckDoesNotSameException(ErrorCode errorCode) {
        super(errorCode);
    }
}
