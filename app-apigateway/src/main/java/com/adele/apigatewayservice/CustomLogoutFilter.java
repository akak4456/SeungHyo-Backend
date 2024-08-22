package com.adele.apigatewayservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class CustomLogoutFilter extends AbstractGatewayFilterFactory<CustomLogoutFilter.Config> {
    Environment env;
    private final RedisUtil redisUtil;
    private final JwtTokenProvider jwtTokenProvider;

    public CustomLogoutFilter(Environment env, RedisUtil redisUtil, JwtTokenProvider jwtTokenProvider) {
        super(CustomLogoutFilter.Config.class);
        this.env = env;
        this.redisUtil = redisUtil;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public static class Config {
        //
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // 요청 헤더에 "Authorization" 헤더가 포함되어 있는지 확인한다.
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                // "Authorization" 헤더가 없는 경우, UNAUTHORIZED(401) 상태로 에러 응답을 반환.
                return onError(exchange, "No Authorization header", HttpStatus.UNAUTHORIZED);
            }

            String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String jwt = authorizationHeader.replace("Bearer ", "");

            if (jwtTokenProvider.validateToken(jwt)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(jwt);
                String memberId = authentication.getName();
                redisUtil.saveBlackList(memberId, jwt, jwtTokenProvider.getAccessTokenValidityInSeconds());
                ServerHttpRequest newRequest = request.mutate()
                        .build();

                return chain.filter(exchange.mutate().request(newRequest).build());
            } else {
                return onError(exchange, "jwt token is not valid", HttpStatus.UNAUTHORIZED);
            }
        };
    }

    // Mono, Flux -> Spring WebFlux (기존의 SpringMVC 방식이 아니기때문에 Servlet 을 사용하지 않음)
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        log.error(err);
        return response.setComplete();
    }
}
