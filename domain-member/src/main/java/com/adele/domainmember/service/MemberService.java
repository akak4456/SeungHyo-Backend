package com.adele.domainmember.service;

import com.adele.domainmember.dto.*;
import com.adele.domainredis.dto.JwtToken;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface MemberService extends UserDetailsService {
    /**
     * 로그인 AuthService
     * @param loginRequest member 아이디, pw
     * @return JwtToken 검증 성공 시 JWT 토큰
     */
    JwtToken login(LoginRequest loginRequest);

    /**
     * 회원가입 시도 AuthService
     * @param joinRequest
     * <ul>
     *     <li><b>memberId</b> 회원가입 시도할 아이디</li>
     *     <li><b>memberPw</b> 회원가입 시도할 비밀번호</li>
     *     <li><b>memberPwCheck</b> 회원가입 시도할 비밀번호 확인</li>
     *     <li><b>statusMessage</b> 회원가입 시도할 상태 메시지</li>
     *     <li><b>email</b> 회원가입 시도할 이메일</li>
     * </ul>
     */
    void join(JoinRequest joinRequest);

    /**
     * id 가 중복되는지 확인하는 service
     * @param id 중복을 체크할 id
     * @return id 가 중복되는지 여부
     */
    boolean isIdExist(String id);

    /**
     * email 이 중복되는지 확인하는 service
     * @param email 중복을 체크할 email
     * @return email 이 중복되는지 여부
     */
    boolean isEmailExist(String email);

    /**
     * 회원정보 조회 service
     * @param memberId 조회할 아이디
     * @return InfoEditResultDTO 조회 결과
     */
    GetInfoEditResponse getInfoEdit(String memberId);

    /**
     * 회원정보 수정 service
     * @param dto 수정할 데이터
     */
    void patchInfoEdit(PatchInfoEditRequest dto);

    /**
     * 비밀번호 일치 여부 확인 service
     * @param id: 비밀번호를 확인할 유저 id
     * @param pw: 확인할 비밀번호
     * @return 비밀번호가 일치하는지 여부
     */
    boolean isPwMatch(String id,String pw);

    /**
     * 비밀번호 변경 서비스
     * @param memberId 비밀번호 변경하고자 하는 member id 
     * @param request request
     */
    void changePw(String memberId, ChangePwRequest request);

    /**
     * 회원탈퇴 service
     * @param memberId 회원탈퇴할 id
     * @return 회원탈퇴 성공 여부
     */
    boolean withdraw(String memberId);

    JwtToken reissue(String refreshToken);

    /**
     * member id 에 해당하는 요약된 정보를 얻어오는 service
     * @param memberId 요약된 정보를 얻어올 id
     * @return 요약된 정보
     */
    InfoResponse getInfo(String memberId);
}
