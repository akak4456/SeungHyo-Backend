package com.adele.memberservice.controller;

import com.adele.memberservice.JwtTokenProvider;
import com.adele.memberservice.common.AuthHeaderConstant;
import com.adele.memberservice.dto.*;
import com.adele.memberservice.properties.EmailConfigProperties;
import com.adele.memberservice.service.EmailCheckCodeService;
import com.adele.memberservice.service.EmailService;
import com.adele.memberservice.service.MemberService;
import com.adele.memberservice.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {
    private final MemberService memberService;
    private final RefreshTokenService refreshTokenService;
    private final EmailService emailService;
    private final EmailCheckCodeService emailCheckCodeService;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailConfigProperties emailConfigProperties;

    /**
     * login 을 처리하는 API
     * login 성공 시 JWT 토큰을 반환
     * login 실패 시 에러가 반환된다.
     * @param loginRequest
     * <ul>
     *     <li><b>memberId</b> 로그인할 id</li>
     *     <li><b>memberPw</b> 로그인할 pw</li>
     * </ul>
     * @return LoginResponse
     * <ul>
     *     <li><b>grantType</b> grant type</li>
     *     <li><b>accessToken</b> access token</li>
     *     <li><b>refreshToken</b> refresh token</li>
     * </ul>
     */
    @PostMapping("/auth/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest loginRequest) {
        // TODO 회원탈퇴한 유저 같은 경우 로그인이 되지 않도록 변경하기
        LoginResponse response = memberService.login(loginRequest);
        refreshTokenService.saveRefreshToken(loginRequest.getMemberId(), response.getRefreshToken());
        return response;
    }

    /**
     * 이메일 체크코드를 보낸다
     * @param sendCheckCodeEmailRequest
     * <ul>
     *  <li><b>toEmail</b>: 인증코드를 보낼 이메일</li>
     * </ul>
     * @return SendCheckCodeEmailResponse
     * <ul>
     *     <li><b>validDuration</b> 이메일 인증 코드 유효 시간(단위 초)</li>
     * </ul>
     */
    @PostMapping("/auth/send-email-check-code")
    public SendCheckCodeEmailResponse sendEmailCheckCode(@RequestBody @Valid SendCheckCodeEmailRequest sendCheckCodeEmailRequest) {
        SendCheckCodeEmailResponse result = new SendCheckCodeEmailResponse();
        String code = createCode();
        emailCheckCodeService.saveEmailCheckCode(sendCheckCodeEmailRequest.getToEmail(), code, emailConfigProperties.getEmailCheckCodeValidInSeconds());
        // TODO 예쁜 이메일 보내기
        emailService.sendMail(new EmailMessage(
                sendCheckCodeEmailRequest.getToEmail(),
                "인증번호",
                code
        ));
        result.setValidDuration(emailConfigProperties.getEmailCheckCodeValidInSeconds());
        return result;
    }

    // 인증번호 및 임시 비밀번호 생성 메서드 TODO 보안 고려하기
    private String createCode() {
        Random random = new Random();
        StringBuffer key = new StringBuffer();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(4);

            switch (index) {
                case 0: key.append((char) ((int) random.nextInt(26) + 97)); break;
                case 1: key.append((char) ((int) random.nextInt(26) + 65)); break;
                default: key.append(random.nextInt(9));
            }
        }
        return key.toString();
    }

    /**
     * 이메일 체크 코드 확인
     * @param validEmailDTO
     * <ul>
     *  <li><b>email</b>: 인증코드 확인할 이메일</li>
     *  <li><b>code</b>: 인증코드</li>
     * </ul>
     * @return ValidEmailResponse
     * <ul>
     *     <li><b>emailValidForm</b> 이메일이 유효한 폼인지</li>
     *     <li><b>codeValidForm</b> 코드가 유효한 폼인지</li>
     *     <li><b>isEmailValid</b> 이메일이 유효한 주소인지</li>
     * </ul>
     */
    @PostMapping("/auth/valid-email")
    public void validEmail(@RequestBody @Valid ValidEmailRequest validEmailDTO) {
        emailCheckCodeService.testCheckCodeCorrect(validEmailDTO.getEmail(), validEmailDTO.getCode());
        emailCheckCodeService.saveValidEmail(validEmailDTO.getEmail());
    }

    /**
     * logout 을 처리하는 API
     * logout 을 access token 을 redis black list 에 추가하는 방식으로 이루어진다.
     * @param  logoutRequest request body
     * <ul>
     *  <li><b>accessToken</b>: 로그아웃할 access token</li>
     *  <li><b>refreshToken</b>: 로그아웃할 refresh token</li>
     * </ul>
     */
    @PatchMapping("/auth/logout")
    public void logout(@RequestBody @Valid LogoutRequest logoutRequest) {
        refreshTokenService.deleteRefreshToken(logoutRequest.getRefreshToken());
    }

    /**
     * 회원가입 시도
     * 회원가입 시도 성공, 실패 여부를 반환한다.
     * @param joinRequest
     * <ul>
     *     <li><b>memberId</b> 회원가입 시도할 아이디</li>
     *     <li><b>memberPw</b> 회원가입 시도할 비밀번호</li>
     *     <li><b>memberPwCheck</b> 회원가입 시도할 비밀번호 확인</li>
     *     <li><b>statusMessage</b> 회원가입 시도할 상태 메시지</li>
     *     <li><b>email</b> 회원가입 시도할 이메일</li>
     * </ul>
     */
    @PostMapping("/auth/join")
    public void join(@RequestBody @Valid JoinRequest joinRequest) {
        emailCheckCodeService.testValidEmail(joinRequest.getEmail());
        memberService.join(joinRequest);
    }

    /**
     * reissue 를 시도한다.
     * 클라이언트가 시도할 일은 없고 주로 JWT Filter 에서 access token 이 만료되었을 때 호출된다
     * @param refreshToken refresh token
     * @return String 새로운 jwt access token
     */
    @PostMapping("/auth/reissue")
    public void reissue(@RequestHeader("Refresh-Token") String refreshToken, HttpServletResponse response) {
        LoginResponse token = memberService.reissue(refreshToken);
        String memberId = jwtTokenProvider.getAuthentication(token.getAccessToken()).getName();
        log.info(memberId);
        refreshTokenService.saveRefreshToken(memberId, token.getRefreshToken());
        response.addHeader("Authorization", "Bearer " + token.getAccessToken());
        response.addHeader("Refresh-Token", refreshToken);
        response.addHeader("new-refresh-token", token.getRefreshToken());
    }

    /**
     * 정보 수정에서 이용할 데이터를 조회한다
     * @return InfoEditResultDTO
     * <ul>
     *     <li><b>memberId</b> 조회한 member id</li>
     *     <li><b>statusMessage</b> 조회한 상태 메시지</li>
     *     <li><b>email</b> 조회한 이메일</li>
     * </ul>
     */
    @GetMapping("/my/info-edit")
    public GetInfoEditResponse getInfoEdit(@RequestHeader(AuthHeaderConstant.AUTH_USER) String memberId) {
        return memberService.getInfoEdit(memberId);
    }

    /**
     * 정보 수정을 시도한다
     * @param dto
     * <ul>
     *     <li><b>memberId</b> 정보 수정할 id</li>
     *     <li><b>memberPw</b> 정보 수정할 pw. pw 는 정보 수정할 때 체크하는 용도로 쓰인다</li>
     *     <li><b>statusMessage</b> 정보 수정할 상태 메시지</li>
     *     <li><b>email</b> 정보 수정할 email</li>
     * </ul>
     */
    @PatchMapping("/my/info-edit")
    public void patchInfoEdit(@RequestHeader(AuthHeaderConstant.AUTH_USER) String memberId, @RequestBody @Valid PatchInfoEditRequest dto) {
        memberService.patchInfoEdit(dto);
    }

    /**
     * 비밀번호 변경을 시도한다
     * @param dto
     * <ul>
     *     <li><b>currentPw</b> 입력한 현재 비밀번호</li>
     *     <li><b>newPw</b> 입력한 새 비밀번호</li>
     *     <li><b>newPwCheck</b> 입력한 새 비밀번호 확인</li>
     * </ul>
     * @return ChangePwResultDTO
     * <ul>
     *     <li><b>currentPwValidForm</b> 현재 비밀번호가 유효한 폼인지</li>
     *     <li><b>newPwValidForm</b> 새로운 비밀번호가 유효한 폼인지</li>
     *     <li><b>newPwCheckValidForm</b> 새로운 비밀번호 확인이 유효한 폼인지</li>
     *     <li><b>currentPwMatch</b> 현재 비밀번호와 유저 비밀번호가 일치하는지</li>
     *     <li><b>currentPwAndNewPwNotMatch</b> 현재 비밀번호와 새 비밀번호가 일치하지 않는지</li>
     *     <li><b>newPwMatch</b> 새 비밀번호와 새비밀번호 확인이 일치하는지</li>
     * </ul>
     */
    @PatchMapping("/my/change-pw")
    public void changePw(@RequestHeader(AuthHeaderConstant.AUTH_USER) String memberId, @RequestBody @Valid ChangePwRequest dto) {
        memberService.changePw(memberId, dto);
    }

    /**
     * 회원탈퇴를 한다
     * @return 회원탈퇴 성공 여부
     */
    @DeleteMapping("/my/withdraw")
    public Boolean withdraw(@RequestHeader(AuthHeaderConstant.AUTH_USER) String memberId) {
        return memberService.withdraw(memberId);
    }

}
