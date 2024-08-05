package com.adele.seunghyobackend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 1. Request Header에서 JWT 토큰 추출
        String accessToken = getAccessToken((HttpServletRequest) request);
        String refreshToken = getRefreshToken((HttpServletRequest) request);

        // 2. validateToken으로 토큰 유효성 검사
        if (accessToken != null) {
            if(jwtTokenProvider.validateToken(accessToken)) {
                // access 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext에 저장
                Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else if(refreshToken != null) {
                boolean isRefreshToken = jwtTokenProvider.refreshTokenValidation(refreshToken);
                log.info(isRefreshToken + "");
                if(isRefreshToken) {
                    Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);
                    String newAccessToken = jwtTokenProvider.generateToken(authentication).getAccessToken();
                    ((HttpServletResponse) response).setHeader("Authorization", "Bearer " + newAccessToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        chain.doFilter(request, response);
    }

    // Request Header에서 access 토큰 정보 추출
    private String getAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private String getRefreshToken(HttpServletRequest request) {
        return request.getHeader("Refresh-Token");
    }
}
