package com.adele.memberservice.service.impl;

import com.adele.memberservice.properties.EmailConfigProperties;
import com.adele.memberservice.service.EmailCheckCodeService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class EmailCheckCodeServiceImpl implements EmailCheckCodeService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final long validEmailTimeInSeconds;

    public EmailCheckCodeServiceImpl(
            RedisTemplate<String, Object> redisTemplate,
            @Autowired EmailConfigProperties emailConfigProperties
    ) {
        this.redisTemplate = redisTemplate;
        this.validEmailTimeInSeconds = emailConfigProperties.getValidEmailTimeInSeconds();
    }

    @Override
    public void saveEmailCheckCode(String memberEmail, String checkCode, long emailCheckCodeValidTimeInSeconds) {
        redisTemplate.opsForValue().set("CK:"+memberEmail, checkCode, emailCheckCodeValidTimeInSeconds, TimeUnit.SECONDS);
    }

    @Override
    public boolean isCheckCodeCorrect(String memberEmail, String checkCode) {
        String storedCheckCode = (String)redisTemplate.opsForValue().get("CK:"+memberEmail);
        return checkCode.equals(storedCheckCode);
    }

    @Override
    public void saveValidEmail(String memberEmail) {
        redisTemplate.opsForValue().set("VALID:"+memberEmail, "VALID", validEmailTimeInSeconds, TimeUnit.SECONDS);
    }

    @Override
    public boolean isValidEmail(String memberEmail) {
        return Objects.equals(redisTemplate.opsForValue().get("VALID:" + memberEmail), "VALID");
    }

}
