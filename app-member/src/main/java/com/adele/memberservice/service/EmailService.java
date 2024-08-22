package com.adele.memberservice.service;

import com.adele.memberservice.dto.EmailMessage;

public interface EmailService {
    /**
     * EmailMessage 에 담겨져 있는 데이터를 이용해서
     * email 을 보낸다
     * @param emailMessage
     */
    void sendMail(EmailMessage emailMessage);
}
