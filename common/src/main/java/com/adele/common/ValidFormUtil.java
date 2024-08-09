package com.adele.common;

public class ValidFormUtil {
    public static boolean validatePwForm(String pw) {
        return pw != null && !pw.isBlank();
    }

    public static boolean validateStatusMessageForm(String statusMessage) {
        return statusMessage != null && !statusMessage.isBlank();
    }

    public static boolean validateIdForm(String id) {
        return id != null && !id.isBlank();
    }

    public static boolean validateEmailForm(String email) {
        return email != null && !email.isBlank();
    }
}
