package com.adele.apigatewayservice;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtil {
    private final RedisTemplate<String, Object> redisTemplate;

    public void saveBlackList(String memberId, String jwt, Long accessTokenValid) {
        redisTemplate.opsForValue().set("blacklist:" + memberId, jwt, accessTokenValid, TimeUnit.SECONDS);
    }

    public boolean isBlackList(String memberId, String jwt) {
        String storedJwt = (String)redisTemplate.opsForValue().get("blacklist:"+memberId);
        return jwt.equals(storedJwt);
    }
}
