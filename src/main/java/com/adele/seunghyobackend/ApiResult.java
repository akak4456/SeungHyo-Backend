package com.adele.seunghyobackend;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
