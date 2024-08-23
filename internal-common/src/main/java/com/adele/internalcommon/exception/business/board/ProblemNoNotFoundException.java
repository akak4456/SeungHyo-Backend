package com.adele.internalcommon.exception.business.board;

import com.adele.internalcommon.exception.business.BusinessException;
import com.adele.internalcommon.response.ErrorCode;

public class ProblemNoNotFoundException extends BusinessException {
    public ProblemNoNotFoundException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public ProblemNoNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
