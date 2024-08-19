package com.adele.memberservice.controller;

import com.adele.common.ApiResult;
import com.adele.common.AuthHeaderConstant;
import com.adele.common.ResponseCode;
import com.adele.memberservice.JwtTokenProvider;
import com.adele.memberservice.dto.*;
import com.adele.memberservice.properties.EmailConfigProperties;
import com.adele.memberservice.service.EmailCheckCodeService;
import com.adele.memberservice.service.EmailService;
import com.adele.memberservice.service.MemberService;
import com.adele.memberservice.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
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
     * @return JwtToken
     * <ul>
     *     <li><b>memberIdValidForm</b> id가 유효한 폼인지 여부</li>
     *     <li><b>memberPwValidForm</b> pw가 유효한 폼인지 여부</li>
     *     <li><b>grantType</b> grant type</li>
     *     <li><b>accessToken</b> access token</li>
     *     <li><b>refreshToken</b> refresh token</li>
     * </ul>
     */
    @PostMapping("/auth/login")
    public ApiResult<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest, Errors errors) {
        LoginResponse result = new LoginResponse();
        if(errors.hasErrors()) {
            for(FieldError error : errors.getFieldErrors()) {
                String fieldName = error.getField();
                String setterName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1) + "ValidForm";
                try {
                    Method setter = LoginResponse.class.getMethod(setterName, Boolean.class);
                    setter.invoke(result, false);
                } catch (Exception e) {
                    log.error("error occur but not handle because this error is tiny", e);
                }
            }
        } else {
            // TODO 회원탈퇴한 유저 같은 경우 로그인이 되지 않도록 변경하기
            JwtToken token = memberService.login(loginRequest);
            refreshTokenService.saveRefreshToken(loginRequest.getMemberId(), token.getRefreshToken());
            result.setGrantType(token.getGrantType());
            result.setAccessToken(token.getAccessToken());
            result.setRefreshToken(token.getRefreshToken());
        }
        return ApiResult.<LoginResponse>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .message("로그인 성공")
                .data(result)
                .build();
    }

    /**
     * 이메일 체크코드를 보낸다
     * @param sendCheckCodeEmailRequest
     * <ul>
     *  <li><b>toEmail</b>: 인증코드를 보낼 이메일</li>
     * </ul>
     * @return SendCheckCodeEmailResponse
     * <ul>
     *     <li><b>emailValidForm</b> 이메일이 유효한 폼인지</li>
     *     <li><b>validDuration</b> 이메일 인증 코드 유효 시간(단위 초)</li>
     * </ul>
     */
    @PostMapping("/auth/send-email-check-code")
    public ApiResult<SendCheckCodeEmailResponse> sendEmailCheckCode(@RequestBody @Valid SendCheckCodeEmailRequest sendCheckCodeEmailRequest, Errors errors) {
        SendCheckCodeEmailResponse result = new SendCheckCodeEmailResponse();
        if(errors.hasErrors()) {
            for(FieldError error : errors.getFieldErrors()) {
                String fieldName = error.getField();
                String setterName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1) + "ValidForm";
                try {
                    Method setter = SendCheckCodeEmailResponse.class.getMethod(setterName, Boolean.class);
                    setter.invoke(result, false);
                } catch (Exception e) {
                    log.error("error occur but not handle because this error is tiny", e);
                }
            }
        } else {
            String code = createCode();
            emailCheckCodeService.saveEmailCheckCode(sendCheckCodeEmailRequest.getToEmail(), code, emailConfigProperties.getEmailCheckCodeValidInSeconds());
            // TODO 예쁜 이메일 보내기
            emailService.sendMail(new EmailMessage(
                    sendCheckCodeEmailRequest.getToEmail(),
                    "인증번호",
                    code
            ));
            result.setValidDuration(emailConfigProperties.getEmailCheckCodeValidInSeconds());
        }
        return ApiResult.<SendCheckCodeEmailResponse>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .message("이메일 전송 성공")
                .data(result)
                .build();
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
    public ApiResult<ValidEmailResponse> validEmail(@RequestBody @Valid ValidEmailRequest validEmailDTO, Errors errors) {
        ValidEmailResponse result = new ValidEmailResponse();
        if(errors.hasErrors()) {
            for(FieldError error : errors.getFieldErrors()) {
                String fieldName = error.getField();
                String setterName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1) + "ValidForm";
                try {
                    Method setter = ValidEmailResponse.class.getMethod(setterName, Boolean.class);
                    setter.invoke(result, false);
                } catch (Exception e) {
                    log.error("error occur but not handle because this error is tiny", e);
                }
            }
        } else {
            boolean isValidEmail = emailCheckCodeService.isCheckCodeCorrect(validEmailDTO.getEmail(), validEmailDTO.getCode());
            result.setIsEmailValid(isValidEmail);
            if (isValidEmail) {
                emailCheckCodeService.saveValidEmail(validEmailDTO.getEmail());
            }
        }
        return ApiResult.<ValidEmailResponse>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .message("이메일 체크 확인 성공")
                .data(result)
                .build();
    }

    /**
     * logout 을 처리하는 API
     * logout 을 access token 을 redis black list 에 추가하는 방식으로 이루어진다.
     */
    @PatchMapping("/auth/logout")
    public ApiResult<Void> logout(@RequestBody LogoutRequest logoutRequest) {
        refreshTokenService.deleteRefreshToken(logoutRequest.getRefreshToken());
        return ApiResult.<Void>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .message("로그아웃 성공")
                .build();
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
    @PostMapping("/auth/join")
    public ApiResult<JoinResponse> join(@RequestBody @Valid JoinRequest joinRequest, Errors errors) {
        JoinResponse result = new JoinResponse();
        if(errors.hasErrors()) {
            for(FieldError error : errors.getFieldErrors()) {
                String fieldName = error.getField();
                String setterName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1) + "ValidForm";
                try {
                    Method setter = JoinResponse.class.getMethod(setterName, Boolean.class);
                    setter.invoke(result, false);
                } catch (Exception e) {
                    log.error("error occur but not handle because this error is tiny", e);
                }
            }
        } else {
            boolean isAvailable = true;
            if(joinRequest.getMemberPw() == null || !joinRequest.getMemberPw().equals(joinRequest.getMemberPwCheck())) {
                isAvailable = false;
                result.setPwAndPwCheckSame(false);
            }
            if(memberService.isIdExist(joinRequest.getMemberId())) {
                isAvailable = false;
                result.setIdNotDuplicate(false);
            }
            if(memberService.isEmailExist(joinRequest.getEmail())) {
                isAvailable = false;
                result.setEmailNotDuplicate(false);
            }
            if(!emailCheckCodeService.isValidEmail(joinRequest.getEmail())) {
                isAvailable = false;
                result.setEmailValidate(false);
            }
            if(isAvailable) {
                memberService.join(joinRequest);
            }
        }
        return ApiResult.<JoinResponse>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .message("회원가입 시도 성공")
                .data(result)
                .build();
    }

    /**
     * reissue 를 시도한다.
     * 클라이언트가 시도할 일은 없고 주로 JWT Filter 에서 access token 이 만료되었을 때 호출된다
     * @param refreshToken refresh token
     * @return String 새로운 jwt access token
     */
    @PostMapping("/auth/reissue")
    public String reissue(@RequestHeader("Refresh-Token") String refreshToken, HttpServletResponse response) {
        JwtToken token = memberService.reissue(refreshToken);
        String memberId = jwtTokenProvider.getAuthentication(token.getAccessToken()).getName();
        log.info(memberId);
        refreshTokenService.saveRefreshToken(memberId, token.getRefreshToken());
        response.addHeader("Authorization", "Bearer " + token.getAccessToken());
        response.addHeader("Refresh-Token", refreshToken);
        response.addHeader("new-refresh-token", token.getRefreshToken());
        return token.getAccessToken();
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
    public ApiResult<GetInfoEditResponse> getInfoEdit(@RequestHeader(AuthHeaderConstant.AUTH_USER) String memberId) {
        GetInfoEditResponse result = memberService.getInfoEdit(memberId);
        return ApiResult.<GetInfoEditResponse>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .message("info edit 정보 조회 성공")
                .data(result)
                .build();
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
     * @return PatchInfoEditResponse
     * <ul>
     *     <li><b>memberIdValidForm</b> id 가 유효한 폼인지</li>
     *     <li><b>memberPwValidForm</b> pw 가 유효한 폼인지</li>
     *     <li><b>statusMessageValidForm</b> status 가 유효한 폼인지</li>
     *     <li><b>emailValidForm</b> email 이 유효한 폼인지</li>
     *     <li><b>pwMatch</b> pw 가 일치하는지</li>
     * </ul>
     */
    @PatchMapping("/my/info-edit")
    public ApiResult<PatchInfoEditResponse> patchInfoEdit(@RequestHeader(AuthHeaderConstant.AUTH_USER) String memberId, @RequestBody @Valid PatchInfoEditRequest dto, Errors errors) {
        PatchInfoEditResponse result = new PatchInfoEditResponse();
        if(errors.hasErrors()) {
            for(FieldError error : errors.getFieldErrors()) {
                String fieldName = error.getField();
                String setterName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1) + "ValidForm";
                try {
                    Method setter = PatchInfoEditResponse.class.getMethod(setterName, Boolean.class);
                    setter.invoke(result, false);
                } catch (Exception e) {
                    log.error("error occur but not handle because this error is tiny", e);
                }
            }
        } else {
            boolean isPwMatch = memberService.isPwMatch(dto.getMemberId(), dto.getMemberPw());
            result.setPwMatch(isPwMatch);
            if(isPwMatch) {
                memberService.patchInfoEdit(dto);
            }
        }
        return ApiResult.<PatchInfoEditResponse>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .message("info edit 정보 수정 시도 성공")
                .data(result)
                .build();
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
    public ApiResult<ChangePwResponse> changePw(@RequestHeader(AuthHeaderConstant.AUTH_USER) String memberId, @RequestBody @Valid ChangePwRequest dto, Errors errors) {
        ChangePwResponse result = new ChangePwResponse();
        if(errors.hasErrors()) {
            for(FieldError error : errors.getFieldErrors()) {
                String fieldName = error.getField();
                String setterName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1) + "ValidForm";
                try {
                    Method setter = ChangePwResponse.class.getMethod(setterName, Boolean.class);
                    setter.invoke(result, false);
                } catch (Exception e) {
                    log.error("error occur but not handle because this error is tiny", e);
                }
            }
        } else {
            boolean isCurrentPwMatch = memberService.isPwMatch(memberId, dto.getCurrentPw());
            result.setCurrentPwMatch(isCurrentPwMatch);
            boolean isCurrentPwAndNewPwNotMatch = dto.getCurrentPw() != null && !dto.getCurrentPw().equals(dto.getNewPw());
            result.setCurrentPwAndNewPwNotMatch(isCurrentPwAndNewPwNotMatch);
            boolean isNewPwMatch = dto.getNewPw() != null && dto.getNewPw().equals(dto.getNewPwCheck());
            result.setNewPwMatch(isNewPwMatch);
            if(isCurrentPwMatch && isCurrentPwAndNewPwNotMatch && isNewPwMatch) {
                memberService.changePw(memberId, dto.getNewPw());
            }
        }
        return ApiResult.<ChangePwResponse>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .message("비밀번호 수정 시도 성공")
                .data(result)
                .build();
    }

    /**
     * 회원탈퇴를 한다
     * @return 회원탈퇴 성공 여부
     */
    @DeleteMapping("/my/withdraw")
    public ApiResult<Boolean> withdraw(@RequestHeader(AuthHeaderConstant.AUTH_USER) String memberId) {
        boolean result = memberService.withdraw(memberId);
        return ApiResult.<Boolean>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .message("비밀번호 수정 시도 성공")
                .data(result)
                .build();
    }
}
