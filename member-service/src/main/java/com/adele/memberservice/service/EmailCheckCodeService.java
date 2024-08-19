package com.adele.memberservice.service;

public interface EmailCheckCodeService {
    void saveEmailCheckCode(String memberEmail, String checkCode , long emailCheckCodeValidTimeInSeconds);

    boolean isCheckCodeCorrect(String memberEmail, String checkCode);

    void saveValidEmail(String memberEmail);

    boolean isValidEmail(String memberEmail);
}
