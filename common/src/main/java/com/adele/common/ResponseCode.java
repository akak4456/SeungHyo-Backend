package com.adele.common;

public enum ResponseCode {
    SUCCESS("0");
    private final String code;
    ResponseCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
