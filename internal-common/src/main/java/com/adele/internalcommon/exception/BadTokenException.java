package com.adele.internalcommon.exception;

public class BadTokenException extends RuntimeException{

    public BadTokenException(String message) {
        super(message);
    }

    public BadTokenException(Exception e) {
        super(e);
    }
}
