package com.adele.memberservice.service;

public interface RefreshTokenService {
    void saveRefreshToken(String memberId, String refreshToken);

    boolean validateRefreshToken(String memberId, String refreshToken);

    void deleteRefreshToken(String memberId);
}
