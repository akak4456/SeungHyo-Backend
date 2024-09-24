package com.adele.internalcommon.exception.business.member;

import com.adele.internalcommon.exception.business.BusinessException;
import com.adele.internalcommon.response.ErrorCode;

public class LikeDuplicateException extends BusinessException {
    public LikeDuplicateException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public LikeDuplicateException(ErrorCode errorCode) {
        super(errorCode);
    }
}
