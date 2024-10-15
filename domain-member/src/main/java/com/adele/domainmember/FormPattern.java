package com.adele.domainmember;

public class FormPattern {
    // ID는 영어 대소문자와 숫자만 허용하며, 8~16자
    public static final String ID_PATTERN = "^[a-zA-Z0-9]{8,16}$";
    // PW는 영어 대소문자, 숫자, 특수문자가 반드시 한 개 이상 포함된 8~16자
    public static final String PW_PATTERN = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,16}$";
    public static final String STATUS_PATTERN = "(.|\\s)*\\S(.|\\s)*";
    public static final String EMAIL_PATTERN = "(.|\\s)*\\S(.|\\s)*";
}
