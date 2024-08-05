package com.adele.seunghyobackend.auth.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class EmailCheckCodeService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final long emailCheckCodeValidTimeInSeconds;

    public EmailCheckCodeService(RedisTemplate<String, Object> redisTemplate, @Value("${email-check-code-valid-in-seconds}") long emailCheckCodeValidTimeInSeconds) {
        this.redisTemplate = redisTemplate;
        this.emailCheckCodeValidTimeInSeconds = emailCheckCodeValidTimeInSeconds;
    }

    public void saveEmailCheckCode(String memberEmail, String checkCode) {
        redisTemplate.opsForValue().set("CK:"+memberEmail, checkCode, emailCheckCodeValidTimeInSeconds, TimeUnit.SECONDS);
    }

    public boolean isValidEmail(String memberEmail, String checkCode) {
        String storedCheckCode = (String)redisTemplate.opsForValue().get("CK:"+memberEmail);
        return checkCode.equals(storedCheckCode);
    }

    public long getEmailCheckCodeValidTimeInSeconds() {
        return emailCheckCodeValidTimeInSeconds;
    }
}
