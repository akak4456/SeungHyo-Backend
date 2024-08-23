package com.adele.domainredis.service.impl;


import com.adele.domainredis.EmailConfigProperties;
import com.adele.domainredis.repository.RedisRepository;
import com.adele.domainredis.service.EmailCheckCodeService;
import com.adele.internalcommon.exception.business.EmailCheckCodeNotCorrectException;
import com.adele.internalcommon.exception.business.EmailNotValidException;
import com.adele.internalcommon.response.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class EmailCheckCodeServiceImpl implements EmailCheckCodeService {
    private final RedisRepository redisRepository;
    private final long validEmailTimeInSeconds;

    public EmailCheckCodeServiceImpl(
            RedisRepository redisRepository,
            @Autowired EmailConfigProperties emailConfigProperties
    ) {
        this.redisRepository = redisRepository;
        this.validEmailTimeInSeconds = emailConfigProperties.getValidEmailTimeInSeconds();
    }

    @Override
    public void saveEmailCheckCode(String memberEmail, String checkCode, long emailCheckCodeValidTimeInSeconds) {
        redisRepository.setValue("CK:"+memberEmail, checkCode, emailCheckCodeValidTimeInSeconds, TimeUnit.SECONDS);
    }

    @Override
    public void testCheckCodeCorrect(String memberEmail, String checkCode) {
        String storedCheckCode = (String) redisRepository.getValue("CK:" + memberEmail);
        if(!checkCode.equals(storedCheckCode)) {
            throw new EmailCheckCodeNotCorrectException(ErrorCode.EMAIL_CHECK_CODE_NOT_COORECT);
        }
    }

    @Override
    public void saveValidEmail(String memberEmail) {
        redisRepository.setValue("VALID:"+memberEmail, "VALID", validEmailTimeInSeconds, TimeUnit.SECONDS);
    }

    @Override
    public void testValidEmail(String memberEmail) {
        if(!Objects.equals(redisRepository.getValue("VALID:" + memberEmail), "VALID")) {
            throw new EmailNotValidException(ErrorCode.EMAIL_NOT_VALID);
        }
    }

}
