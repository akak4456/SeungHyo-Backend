package com.adele.seunghyobackend.member.auth.service;

import com.adele.seunghyobackend.member.auth.dto.JoinDTO;
import com.adele.seunghyobackend.member.auth.dto.JoinResultDTO;
import com.adele.seunghyobackend.security.dto.JwtToken;

public interface AuthService {
    /**
     * 로그인 AuthService
     * @param memberId member 아이디
     * @param memberPw member 비밀번호
     * @return JwtToken 검증 성공 시 JWT 토큰
     */
    JwtToken login(String memberId, String memberPw);

    /**
     * 회원가입 시도 AuthService
     * @param joinDTO
     * <ul>
     *     <li><b>memberId</b> 회원가입 시도할 아이디</li>
     *     <li><b>memberPw</b> 회원가입 시도할 비밀번호</li>
     *     <li><b>memberPwCheck</b> 회원가입 시도할 비밀번호 확인</li>
     *     <li><b>statusMessage</b> 회원가입 시도할 상태 메시지</li>
     *     <li><b>email</b> 회원가입 시도할 이메일</li>
     * </ul>
     * @param isEmailValid email 인증했는지 여부
     * @return JoinResultDTO
     * <ul>
     *     <li><b>idNotValidForm</b> id가 올바른 형태가 아닌지 여부</li>
     *     <li><b>idDuplicate</b> id가 중복되었는지 여부</li>
     *     <li><b>statusNotValidForm</b> 상태 메시지가 올바른 형태가 아닌지 여부</li>
     *     <li><b>pwNotValidForm</b> pw가 올바른 형태가 아닌지 여부</li>
     *     <li><b>pwAndPwCheckDifferent</b> 비밀번호와 비밀번호 확인이 다른지 여부</li>
     *     <li><b>emailNotValidForm</b> 이메일이 올바른 형태가 아닌지 여부</li>
     *     <li><b>emailDuplicate</b> 이메일이 중복되었는지 여부</li>
     *     <li><b>emailNotValidate</b> 이메일을 인증했는지 여부</li>
     * </ul>
     */
    JoinResultDTO tryJoin(JoinDTO joinDTO, boolean isEmailValid);
}
