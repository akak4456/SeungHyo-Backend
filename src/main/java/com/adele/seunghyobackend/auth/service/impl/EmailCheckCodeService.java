package com.adele.seunghyobackend.auth.service.impl;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class EmailCheckCodeService {
    private final RedisTemplate<String, Object> redisTemplate;
    @Getter
    private final long emailCheckCodeValidTimeInSeconds;
    private final long validEmailTimeInSeconds;

    public EmailCheckCodeService(
            RedisTemplate<String, Object> redisTemplate,
            @Value("${email-check-code-valid-in-seconds}") long emailCheckCodeValidTimeInSeconds,
            @Value("${valid-email-time-in-seconds}") long validEmailTimeInSeconds
    ) {
        this.redisTemplate = redisTemplate;
        this.emailCheckCodeValidTimeInSeconds = emailCheckCodeValidTimeInSeconds;
        this.validEmailTimeInSeconds = validEmailTimeInSeconds;
    }

    public void saveEmailCheckCode(String memberEmail, String checkCode) {
        redisTemplate.opsForValue().set("CK:"+memberEmail, checkCode, emailCheckCodeValidTimeInSeconds, TimeUnit.SECONDS);
    }

    public boolean isCheckCodeCorrect(String memberEmail, String checkCode) {
        String storedCheckCode = (String)redisTemplate.opsForValue().get("CK:"+memberEmail);
        return checkCode.equals(storedCheckCode);
    }

    public void saveValidEmail(String memberEmail) {
        redisTemplate.opsForValue().set("VALID:"+memberEmail, "VALID", validEmailTimeInSeconds, TimeUnit.SECONDS);
    }

    public boolean isValidEmail(String memberEmail) {
        return Objects.equals(redisTemplate.opsForValue().get("VALID:" + memberEmail), "VALID");
    }

}
