package com.adele.domainredis.service;

public interface EmailCheckCodeService {
    void saveEmailCheckCode(String memberEmail, String checkCode , long emailCheckCodeValidTimeInSeconds);

    void testCheckCodeCorrect(String memberEmail, String checkCode);

    void saveValidEmail(String memberEmail);

    void testValidEmail(String memberEmail);
}
