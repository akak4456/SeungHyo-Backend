package com.adele.domainredis.repository;

import java.util.concurrent.TimeUnit;

public interface RedisRepository {
    void setValue(String key, String value, long validTime, TimeUnit timeUnit);

    Object getValue(String key);

    void deleteValue(String key);
}
