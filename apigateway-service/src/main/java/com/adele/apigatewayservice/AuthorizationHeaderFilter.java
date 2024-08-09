package com.adele.apigatewayservice;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {
    Environment env;

    public AuthorizationHeaderFilter(Environment env) {
        super(Config.class);
        this.env = env;
    }

    public static class Config {
        //
    }
    @Override
    public GatewayFilter apply(Config config) {
        // ServerWebExchange 파라미터는 필터가 동작하는 동안 현재 요청 및 응답에 대한 정보를 제공한다.
        // 비동기 서버 Netty 에서는 동기 서버(ex:tomcat)와 다르게 request/response 객체를 선언할 때 Server~ 를 사용한다.
        GatewayFilter filter = (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // 요청 헤더에 "Authorization" 헤더가 포함되어 있는지 확인한다.
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                // "Authorization" 헤더가 없는 경우, UNAUTHORIZED(401) 상태로 에러 응답을 반환.
                return onError(exchange, "No Authorization header", HttpStatus.UNAUTHORIZED);
            }

            // "Authorization" 헤더에서 JWT 토큰을 추출.
            String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String jwt = authorizationHeader.replace("Bearer", "");

            // 추출한 JWT 토큰의 유효성을 확인.
            if (!isJwtValid(jwt)) {
                return onError(exchange, "JWT Token is not valid", HttpStatus.UNAUTHORIZED);
            }

            // JWT 토큰이 유효한 경우, 다음 필터로 요청을 전달.
            return chain.filter(exchange);
        };

        return filter;
    }

    private boolean isJwtValid(String jwt) {
        // 반환값으로 사용할 boolean 변수를 초기값 true로 설정
        boolean returnValue = true;

        // JWT의 'subject'를 저장할 변수 초기화
        String subject = null;

        try {
            // JWT 토큰을 파싱하고 검증하는 부분
            subject = Jwts.parser()
                    .setSigningKey(env.getProperty("JWT_SECRET")) // 토큰의 비밀 키 설정
                    .parseClaimsJws(jwt) // JWT 토큰 파싱 및 검증
                    .getBody()
                    .getSubject(); // 토큰에서 'subject' 정보 추출

        } catch (Exception e) {
            // 예외가 발생하면 JWT가 유효하지 않다고 판단하고 반환값을 false로 변경
            returnValue = false;
        }

        // 'subject' 값이 비어있으면 JWT가 유효하지 않다고 판단하고 반환값을 false로 변경
        if (subject == null || subject.isEmpty()) {
            returnValue = false;
        }

        // 최종적으로 JWT의 유효성 여부를 나타내는 반환값을 반환
        return returnValue;
    }

    // Mono, Flux -> Spring WebFlux (기존의 SpringMVC 방식이 아니기때문에 Servlet 을 사용하지 않음)
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        log.error(err);
        return response.setComplete();
    }
}
