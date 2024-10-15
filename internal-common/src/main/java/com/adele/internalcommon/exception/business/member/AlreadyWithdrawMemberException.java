package com.adele.internalcommon.exception.business.member;

import com.adele.internalcommon.exception.business.BusinessException;
import com.adele.internalcommon.response.ErrorCode;

public class AlreadyWithdrawMemberException extends BusinessException {
    public AlreadyWithdrawMemberException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public AlreadyWithdrawMemberException(ErrorCode errorCode) {
        super(errorCode);
    }
}
