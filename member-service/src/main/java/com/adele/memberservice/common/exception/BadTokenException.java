package com.adele.memberservice.common.exception;

import com.adele.memberservice.common.ErrorCode;

public class BadTokenException extends RuntimeException{

    public BadTokenException(String message) {
        super(message);
    }

    public BadTokenException(Exception e) {
        super(e);
    }
}
