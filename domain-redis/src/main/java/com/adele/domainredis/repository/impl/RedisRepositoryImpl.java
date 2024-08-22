package com.adele.domainredis.repository.impl;

import com.adele.domainredis.repository.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RedisRepositoryImpl implements RedisRepository {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void setValue(String key, String value, long validTime, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, validTime, TimeUnit.SECONDS);
    }

    @Override
    public Object getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void deleteValue(String key) {
        redisTemplate.delete(key);
    }
}
