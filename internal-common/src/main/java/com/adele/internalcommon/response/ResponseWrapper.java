package com.adele.internalcommon.response;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Objects;
import java.util.Set;

/**
 * https://velog.io/@kylekim2123/SpringBoot-ResponseBodyAdvice%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%9C-%EA%B3%B5%ED%86%B5-%EC%9D%91%EB%8B%B5-%EC%B2%98%EB%A6%AC%EC%99%80-%EA%B4%80%EB%A0%A8-%ED%8A%B8%EB%9F%AC%EB%B8%94-%EC%8A%88%ED%8C%85
 * 를 참고해서 작성함
 */
@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ResponseWrapper implements ResponseBodyAdvice<Object> {
    private final Validator validator;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response
    ) {
        String path = request.getURI().getPath();
        HttpStatus status = null;
        HttpServletResponse servletResponse = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getResponse();
        if (servletResponse != null) {
            int statusCode = servletResponse.getStatus();
            status = HttpStatus.valueOf(statusCode);
        }
        // body 가 null 이라는 것은 void return 이라는 뜻
        // 따라서 별도로 response 를 체크하지 않는다.
        // 근데 이게 과연 맞는 결정일까?
        if(body != null) {
            Set<ConstraintViolation<Object>> violations = validator.validate(body);
            if (!violations.isEmpty()) {
                response.setStatusCode(ErrorCode.RESPONSE_NOT_VALID.getStatus());
                return ApiResponse.builder()
                        .status(ErrorCode.RESPONSE_NOT_VALID.getStatus())
                        .path(path)
                        .data(ErrorResponse.of(ErrorCode.RESPONSE_NOT_VALID, ErrorResponse.FieldError.of(violations)))
                        .build();
            }
        }
        return ApiResponse.builder()
                .status(status)
                .path(path)
                .data(body)
                .build();
    }
}
