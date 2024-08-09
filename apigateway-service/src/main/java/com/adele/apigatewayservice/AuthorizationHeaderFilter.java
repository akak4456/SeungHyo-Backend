package com.adele.apigatewayservice;

import com.adele.common.AuthHeaderConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Component
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {
    Environment env;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthorizationHeaderFilter(Environment env, JwtTokenProvider jwtTokenProvider) {
        super(Config.class);
        this.env = env;
        this.jwtTokenProvider = jwtTokenProvider;
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
            String jwt = authorizationHeader.replace("Bearer ", "");

            // 추출한 JWT 토큰의 유효성을 확인.
            Authentication authentication;
            if (jwtTokenProvider.validateToken(jwt)) {
                authentication = jwtTokenProvider.getAuthentication(jwt);
            } else {
                return onError(exchange, "JWT Token is not valid", HttpStatus.UNAUTHORIZED);
            }
            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                    .header(AuthHeaderConstant.AUTH_USER, authentication.getName())
                    .header(AuthHeaderConstant.AUTH_USER_ROLES, authentication.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.joining(",")))
                    .build();

            ServerWebExchange modifiedExchange = exchange.mutate().request(modifiedRequest).build();

            // JWT 토큰이 유효한 경우, 다음 필터로 요청을 전달.
            return chain.filter(modifiedExchange);
        };

        return filter;
    }

    // Mono, Flux -> Spring WebFlux (기존의 SpringMVC 방식이 아니기때문에 Servlet 을 사용하지 않음)
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        log.error(err);
        return response.setComplete();
    }
}
