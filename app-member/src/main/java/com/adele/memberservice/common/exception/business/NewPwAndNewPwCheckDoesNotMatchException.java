package com.adele.memberservice.common.exception.business;

import com.adele.memberservice.common.ErrorCode;

public class NewPwAndNewPwCheckDoesNotMatchException extends BusinessException{
    public NewPwAndNewPwCheckDoesNotMatchException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public NewPwAndNewPwCheckDoesNotMatchException(ErrorCode errorCode) {
        super(errorCode);
    }
}
