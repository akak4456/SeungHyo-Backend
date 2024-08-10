package com.adele.memberservice.controller;

import com.adele.common.ApiResult;
import com.adele.common.AuthHeaderConstant;
import com.adele.common.ResponseCode;
import com.adele.memberservice.dto.*;
import com.adele.memberservice.service.EmailCheckCodeService;
import com.adele.memberservice.service.EmailService;
import com.adele.memberservice.service.MemberService;
import com.adele.memberservice.service.RefreshTokenService;
import com.adele.memberservice.service.impl.EmailCheckCodeServiceImpl;
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
    public ApiResult<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = memberService.login(loginRequest);
        refreshTokenService.saveRefreshToken(loginRequest.getMemberId(), response.getRefreshToken());
        log.info("response: {}", response);
        // TODO 회원탈퇴한 유저 같은 경우 로그인이 되지 않도록 변경하기
        ApiResult<LoginResponse> res = ApiResult.<LoginResponse>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .message("로그인 성공")
                .data(response)
                .build();
        return ApiResult.<LoginResponse>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .message("로그인 성공")
                .data(response)
                .build();
    }

    /**
     * 이메일 체크코드를 보낸다
     * @param emailDTO
     * <ul>
     *  <li><b>toEmail</b>: 인증코드를 보낼 이메일</li>
     * </ul>
     * @return 이메일 인증 코드 유효 시간(단위 초)
     */
    @PostMapping("/auth/send-email-check-code")
    public ApiResult<Long> sendEmailCheckCode(@RequestBody EmailDTO emailDTO) {
        String code = createCode();
        emailCheckCodeService.saveEmailCheckCode(emailDTO.getToEmail(), code);
        // TODO 예쁜 이메일 보내기
        emailService.sendMail(new EmailMessage(
                emailDTO.getToEmail(),
                "인증번호",
                code
        ));
        return ApiResult.<Long>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .message("이메일 전송 성공")
                .data(((EmailCheckCodeServiceImpl)emailCheckCodeService).getEmailCheckCodeValidTimeInSeconds()) // TODO 더 좋은 방법 찾아보기
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
     * @return
     * 이메일 인증이 성공했는지 여부
     */
    @PostMapping("/auth/valid-email")
    public ApiResult<Boolean> validEmail(@RequestBody ValidEmailDTO validEmailDTO) {
        boolean isValidEmail = emailCheckCodeService.isCheckCodeCorrect(validEmailDTO.getEmail(), validEmailDTO.getCode());
        if(isValidEmail) {
            emailCheckCodeService.saveValidEmail(validEmailDTO.getEmail());
        }
        return ApiResult.<Boolean>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .message("이메일 체크 확인 성공")
                .data(isValidEmail)
                .build();
    }

    /**
     * logout 을 처리하는 API
     * logout 을 access token 을 redis black list 에 추가하는 방식으로 이루어진다.
     * TODO redis 를 이용한 logout 구현하기
     */
    @PatchMapping("/auth/logout")
    public ApiResult<Void> logout(@RequestBody LogoutDTO logoutDTO) {
        refreshTokenService.deleteRefreshToken(logoutDTO.getRefreshToken());
        return ApiResult.<Void>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .message("로그아웃 성공")
                .build();
    }

    /**
     * 회원가입 시도
     * 회원가입 시도 성공, 실패 여부를 반환한다.
     * @param joinDTO
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
    public ApiResult<JoinResultDTO> join(@RequestBody JoinDTO joinDTO) {
        JoinResultDTO joinResultDTO = memberService.tryJoin(joinDTO, emailCheckCodeService.isValidEmail(joinDTO.getEmail()));
        return ApiResult.<JoinResultDTO>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .message("회원가입 시도 성공")
                .data(joinResultDTO)
                .build();
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
    public String getInfoEdit(@RequestHeader(AuthHeaderConstant.AUTH_USER) String memberId, @RequestHeader(AuthHeaderConstant.AUTH_USER_ROLES) String roles) {
        log.info("memberId: {}", memberId);
        log.info("roles: {}", roles);
        return "hello";
    }
}
