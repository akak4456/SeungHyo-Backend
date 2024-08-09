package com.adele.memberservice.service;

import com.adele.memberservice.dto.LoginRequest;
import com.adele.memberservice.dto.LoginResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface MemberService extends UserDetailsService {
    /**
     * 로그인 AuthService
     * @param loginRequest member 아이디, pw
     * @return LoginResponse 검증 성공 시 JWT 토큰
     */
    LoginResponse login(LoginRequest loginRequest);
}
