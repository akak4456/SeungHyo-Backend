package com.adele.memberservice.service;

import com.adele.memberservice.dto.*;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface MemberService extends UserDetailsService {
    /**
     * 로그인 AuthService
     * @param loginRequest member 아이디, pw
     * @return LoginResponse 검증 성공 시 JWT 토큰
     */
    LoginResponse login(LoginRequest loginRequest);

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

    /**
     * 회원정보 조회 service
     * @param memberId 조회할 아이디
     * @return InfoEditResultDTO 조회 결과
     */
    InfoEditResultDTO getInfoEdit(String memberId);

    /**
     * 회원정보 수정 service
     * @param dto 수정할 데이터
     * @return PatchInfoEditResultDTO 수정 시도 결과
     */
    PatchInfoEditResultDTO patchInfoEdit(PatchInfoEditDTO dto, boolean idMatch);

    ChangePwResultDTO tryChangePw(String memberId, ChangePwDTO dto);

    /**
     * 회원탈퇴 service
     * @param memberId 회원탈퇴할 id
     * @return 회원탈퇴 성공 여부
     */
    boolean withdraw(String memberId);
}
