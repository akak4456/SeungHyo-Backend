package com.adele.seunghyobackend.service;

import com.adele.seunghyobackend.model.dto.JwtToken;

public interface MemberService {
    /**
     * 로그인 MemberService
     * @param memberId member 아이디
     * @param memberPw member 비밀번호
     * @return JwtToken 검증 성공 시 JWT 토큰
     */
    JwtToken login(String memberId, String memberPw);
}
