package com.adele.memberservice.service.impl;

import com.adele.memberservice.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final long refreshTokenValidityInSeconds;

    public RefreshTokenServiceImpl(RedisTemplate<String, Object> redisTemplate, @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValidityInSeconds) {
        this.redisTemplate = redisTemplate;
        this.refreshTokenValidityInSeconds = refreshTokenValidityInSeconds;
    }

    @Override
    public void saveRefreshToken(String memberId, String refreshToken) {
        redisTemplate.opsForValue().set(memberId, refreshToken, refreshTokenValidityInSeconds, TimeUnit.SECONDS);
    }

    @Override
    public boolean validateRefreshToken(String memberId, String refreshToken) {
        String storedRefreshToken = (String)redisTemplate.opsForValue().get(memberId);
        return refreshToken.equals(storedRefreshToken);
    }

    @Override
    public void deleteRefreshToken(String memberId) {
        redisTemplate.delete(memberId);
    }
}
