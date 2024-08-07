package com.adele.seunghyobackend.exception;

import com.adele.seunghyobackend.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.adele.seunghyobackend.Constant.CODE_LOGIN_FAIL;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResult<Void>> handleBadCredentialsException(BadCredentialsException e) {
        log.error("로그인 실패 error", e);
        return new ResponseEntity<>(ApiResult.<Void>builder()
                .code(CODE_LOGIN_FAIL)
                .message("로그인 실패")
                .build(), HttpStatus.FORBIDDEN);
    }
}
