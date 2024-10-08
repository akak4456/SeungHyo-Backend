package com.adele.domainredis.service;

public interface RefreshTokenService {
    void saveRefreshToken(String memberId, String refreshToken);

    void validateRefreshToken(String memberId, String refreshToken);

    void deleteRefreshToken(String memberId);
}
