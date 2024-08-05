package com.adele.seunghyobackend.security;

import com.adele.seunghyobackend.member.service.impl.RefreshTokenService;
import com.adele.seunghyobackend.security.model.dto.JwtToken;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * 토큰의 생성, 토큰의 유효성 검증 등을 담당한다.
 * https://suddiyo.tistory.com/entry/Spring-Spring-Security-JWT-%EB%A1%9C%EA%B7%B8%EC%9D%B8-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0-2
 * 를 참고해서 구현함
 */
@Slf4j
@Component
public class JwtTokenProvider {
    private static final String AUTHORITIES_KEY = "auth";
    private final long accessTokenValidityInSeconds;
    private final long refreshTokenValidityInSeconds;

    private final Key key;
    private final RefreshTokenService refreshTokenService;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-validity-in-seconds}") long accessTokenValidityInSeconds,
            @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValidityInSeconds,
            RefreshTokenService refreshTokenService) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenValidityInSeconds = accessTokenValidityInSeconds;
        this.refreshTokenValidityInSeconds = refreshTokenValidityInSeconds;
        this.refreshTokenService = refreshTokenService;
    }

    /**
     * 인증(Authentication) 객체를 기반으로 Access Token과 Refresh Token 생성
     * @param authentication spring boot 에서 생성해주는 authentication
     * @return Access Token: 인증된 사용자의 권한 정보와 만료 시간을 담고 있음, Refresh Token: Access Token의 갱신을 위해 사용 됨
     */
    public JwtToken generateToken(Authentication authentication) {
        return generateToken(authentication, LocalDateTime.now());
    }
    public JwtToken generateToken(Authentication authentication, LocalDateTime now) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        LocalDateTime accessTokenExpiresIn = now.plusSeconds(accessTokenValidityInSeconds);
        String accessToken = Jwts.builder()
                .subject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .expiration(Date.from(accessTokenExpiresIn.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(key)
                .compact();
        LocalDateTime refreshTokenExpiresIn = now.plusSeconds(this.refreshTokenValidityInSeconds);
        String refreshToken = Jwts.builder()
                .subject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .expiration(Date.from(refreshTokenExpiresIn.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(key)
                .compact();

        return JwtToken.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

    }

    /**
     * 주어진 token을 복호화하여 사용자의 인증 정보(Authentication)를 생성
     * 토큰의 Claims에서 권한 정보를 추출하고, User 객체를 생성하여 Authentication 객체로 반환
     * Collection<? extends GrantedAuthority>로 리턴받는 이유
     * 권한 정보를 다양한 타입의 객체로 처리할 수 있고, 더 큰 유연성과 확장성을 가질 수 있음
     * @param token token 을 받음
     * @return Authentication 인증 정보
     */
    public Authentication getAuthentication(String token) {
        /*
        [ Authentication 객체 생성하는 과정 ]
        1. 토큰의 클레임에서 권한 정보를 가져옴. "auth" 클레임은 토큰에 저장된 권한 정보를 나타냄
        2. 가져온 권한 정보를 SimpleGrantedAuthority 객체로 변환하여 컬렉션에 추가
        3. UserDetails 객체를 생성하여 주체(subject)와 권한 정보, 기타 필요한 정보를 설정
        4. UsernamepasswordAuthenticationToken 객체를 생성하여 주체와 권한 정보를 포함한 인증(Authentication) 객체를 생성
         */
        // Jwt 토큰 복호화
        Claims claims = parseClaims(token);

        if(claims.get(AUTHORITIES_KEY) == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .toList();

        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    /**
     * 주어진 토큰을 검증하여 유효성을 확인
     * Jwts.parserBuilder를 사용하여 토큰의 서명 키를 설정하고, 예외 처리를 통해 토큰의 유효성 여부를 판단
     * IllegalArgumentException 발생하는 경우
     * 토큰이 올바른 형식이 아니거나 클레임이 비어있는 경우 등에 발생
     * claim.getSubject()는 주어진 토큰의 클레임에서 "sub" 클레임의 값을 반환
     * 토큰의 주체를 나타냄. ex) 사용자의 식별자나 이메일 주소
     * @param token 검증하고자 하는 토큰
     * @return boolean 검증 성공 여부
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

    public boolean refreshTokenValidation(String refreshToken) {
        if(!validateToken(refreshToken)) return false;
        Claims claims = parseClaims(refreshToken);
        String id = claims.getSubject();
        return refreshTokenService.validateRefreshToken(id, refreshToken);
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
    private SecretKey getSignInKey() {
        byte[] keyBytes = key.getEncoded();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, "HmacSHA256");
    }
}
