package com.adele.seunghyobackend.exception;

import com.adele.seunghyobackend.common.ApiResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.adele.seunghyobackend.common.Constant.CODE_LOGIN_FAIL;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResult<Void>> handleBadCredentialsException(BadCredentialsException e) {
        return new ResponseEntity<>(ApiResult.<Void>builder()
                .code(CODE_LOGIN_FAIL)
                .message("로그인 실패")
                .build(), HttpStatus.FORBIDDEN);
    }
}
