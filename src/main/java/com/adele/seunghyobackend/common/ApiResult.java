package com.adele.seunghyobackend.common;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
@ToString
public class ApiResult<T> {
    private String code;
    private String message;
    private T data;

    @Builder
    public ApiResult(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
