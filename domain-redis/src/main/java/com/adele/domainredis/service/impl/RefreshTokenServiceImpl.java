package com.adele.domainredis.service.impl;

import com.adele.domainredis.repository.RedisRepository;
import com.adele.domainredis.service.RefreshTokenService;
import com.adele.internalcommon.exception.BadTokenException;
import com.adele.internalcommon.response.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RedisRepository redisRepository;
    private final long refreshTokenValidityInSeconds;

    public RefreshTokenServiceImpl(RedisRepository redisRepository, @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValidityInSeconds) {
        this.redisRepository = redisRepository;
        this.refreshTokenValidityInSeconds = refreshTokenValidityInSeconds;
    }

    @Override
    public void saveRefreshToken(String memberId, String refreshToken) {
        redisRepository.setValue(memberId, refreshToken, refreshTokenValidityInSeconds, TimeUnit.SECONDS);
    }

    @Override
    public void validateRefreshToken(String memberId, String refreshToken) {
        String storedRefreshToken = (String)redisRepository.getValue(memberId);
        if(!refreshToken.equals(storedRefreshToken)) {
            throw new BadTokenException(ErrorCode.BAD_TOKEN.getMessage());
        }
    }

    @Override
    public void deleteRefreshToken(String memberId) {
        redisRepository.deleteValue(memberId);
    }
}
