package com.adele.apigatewayservice;

import com.adele.domainredis.repository.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtil {
    private final RedisRepository redisRepository;

    public void saveBlackList(String memberId, String jwt, Long accessTokenValid) {
        redisRepository.setValue("blacklist:" + memberId, jwt, accessTokenValid, TimeUnit.SECONDS);
    }

    public boolean isBlackList(String memberId, String jwt) {
        String storedJwt = (String)redisRepository.getValue("blacklist:"+memberId);
        return jwt.equals(storedJwt);
    }
}
