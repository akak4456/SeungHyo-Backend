package com.adele.apigatewayservice;

import com.adele.common.ApiResult;
import com.adele.common.AuthHeaderConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {
    Environment env;
    private final JwtTokenProvider jwtTokenProvider;
    private final WebClient.Builder webClientBuilder;
    private final RedisUtil redisUtil;

    public AuthorizationHeaderFilter(Environment env, JwtTokenProvider jwtTokenProvider, WebClient.Builder webClientBuilder, RedisUtil redisUtil) {
        super(Config.class);
        this.env = env;
        this.jwtTokenProvider = jwtTokenProvider;
        this.webClientBuilder = webClientBuilder;
        this.redisUtil = redisUtil;
    }

    public static class Config {
        //
    }
    @Override
    public GatewayFilter apply(Config config) {
        // ServerWebExchange 파라미터는 필터가 동작하는 동안 현재 요청 및 응답에 대한 정보를 제공한다.
        // 비동기 서버 Netty 에서는 동기 서버(ex:tomcat)와 다르게 request/response 객체를 선언할 때 Server~ 를 사용한다.

        return (exchange, chain) -> {
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
            if (jwtTokenProvider.validateToken(jwt)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(jwt);
                if(redisUtil.isBlackList(authentication.getName(), jwt)) {
                    return onError(exchange, "this token is logouted", HttpStatus.UNAUTHORIZED);
                }
                return chain.filter(getValidWebExchangeFromJWTToken(exchange, authentication, jwt, null));
            } else {
                String refreshToken = request.getHeaders().get("Refresh-Token").get(0);
                return reissueToken(exchange, chain, refreshToken);
            }
        };
    }

    private Mono<Void> reissueToken(ServerWebExchange exchange, GatewayFilterChain chain, String refreshToken) {
        WebClient webClient = webClientBuilder.build();

        return webClient.post()
                .uri( "lb://MEMBER-SERVICE/api/v1/member/auth/reissue")
                .header("Refresh-Token", refreshToken)
                .exchangeToMono((response) -> {
                    String newAccessToken = Objects.requireNonNull(response.headers().asHttpHeaders().get("Authorization")).get(0).substring("Bearer ".length());
                    String newRefreshToken = Objects.requireNonNull(response.headers().asHttpHeaders().get("Refresh-Token")).get(0);
                    Authentication authentication = jwtTokenProvider.getAuthentication(newAccessToken);
                    return chain.filter(getValidWebExchangeFromJWTToken(exchange, authentication, newAccessToken, newRefreshToken));
                })
                .onErrorResume(e -> {
                    log.error("error occured", e);
                    return onError(exchange, "Failed to reissue JWT token", HttpStatus.UNAUTHORIZED);
                });
    }

    private ServerWebExchange getValidWebExchangeFromJWTToken(ServerWebExchange exchange, Authentication authentication, String accessToken, String newRefreshToken) {
        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                .header(AuthHeaderConstant.AUTH_USER, authentication.getName())
                .header(AuthHeaderConstant.AUTH_USER_ROLES, authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(",")))
                .build();
        if(newRefreshToken != null) {
            ServerHttpResponse modifiedResponse = exchange.getResponse();
            modifiedResponse.getHeaders().add("Authorization", "Bearer " + accessToken);
            modifiedResponse.getHeaders().add("Refresh-Token", newRefreshToken);
            return exchange.mutate().request(modifiedRequest).response(modifiedResponse).build();
        } else {
            return exchange.mutate().request(modifiedRequest).build();
        }
    }

    // Mono, Flux -> Spring WebFlux (기존의 SpringMVC 방식이 아니기때문에 Servlet 을 사용하지 않음)
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        log.error(err);
        return response.setComplete();
    }
}
