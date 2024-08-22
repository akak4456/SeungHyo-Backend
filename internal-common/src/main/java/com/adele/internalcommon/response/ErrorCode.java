package com.adele.internalcommon.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // Common
    INVALID_INPUT_VALUE("C001", "Invalid Input Value", HttpStatus.BAD_REQUEST),
    METHOD_NOT_ALLOWED("C002", " Invalid Input Value", HttpStatus.METHOD_NOT_ALLOWED),
    ENTITY_NOT_FOUND("C003", " Entity Not Found", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR("C004", "Server Error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_TYPE_VALUE( "C005", " Invalid Type Value", HttpStatus.BAD_REQUEST),
    HANDLE_ACCESS_DENIED("C006", "Access is Denied", HttpStatus.FORBIDDEN),
    BAD_TOKEN("C007", "Bad Access Token or Refresh Token", HttpStatus.BAD_REQUEST),
    RESPONSE_NOT_VALID("C008", "Response Not Valid", HttpStatus.INTERNAL_SERVER_ERROR),

    // Member
    CURRENT_PW_NOT_MATCH("M001", "Current Password Does Not Match", HttpStatus.BAD_REQUEST),
    EMAIL_CHECK_CODE_NOT_COORECT("M002", "Email Check Code Not Correct", HttpStatus.BAD_REQUEST),
    PW_AND_PW_CHECK_DOES_NOT_SAME("M003", "PW and PW Does Not Same", HttpStatus.BAD_REQUEST),
    ID_DUPLICATE("M004", "Id Duplicate", HttpStatus.BAD_REQUEST),
    EMAIL_DUPLICATE("M005", "Email Duplicate", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_VALID("M006", "Email Not Valid", HttpStatus.BAD_REQUEST),
    CURRENT_PW_AND_NEW_PW_MATCH_EXCEPTION("M007", "Current Password And New Password Match", HttpStatus.BAD_REQUEST),
    NEW_PW_AND_NEW_PW_CHECK_DOES_NOT_MATCH("M008", "New Password Does Not Match", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;

    ErrorCode(final String code, final String message, final HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
