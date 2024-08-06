package com.adele.seunghyobackend.auth.controller;

import com.adele.seunghyobackend.auth.dto.*;
import com.adele.seunghyobackend.auth.service.impl.EmailCheckCodeService;
import com.adele.seunghyobackend.common.ApiResult;
import com.adele.seunghyobackend.email.dto.EmailMessage;
import com.adele.seunghyobackend.email.service.EmailService;
import com.adele.seunghyobackend.auth.service.impl.RefreshTokenService;
import com.adele.seunghyobackend.security.JwtTokenProvider;
import com.adele.seunghyobackend.security.model.dto.JwtToken;
import com.adele.seunghyobackend.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

import static com.adele.seunghyobackend.common.Constant.CODE_SUCCESS;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final EmailService emailService;
    private final EmailCheckCodeService emailCheckCodeService;

    /**
     * login 을 처리하는 API
     * login 성공 시 JWT 토큰을 반환
     * login 실패 시 에러가 반환된다.
     * @param loginDTO the data transfer object containing login credentials
     *                 <ul>
     *                   <li><b>memberId</b>: 로그인할 유저 id</li>
     *                   <li><b>memberPw</b>: 로그인할 유저 pw</li>
     *                 </ul>
     * @return an ApiResult containing the JWT token information, including:
     *         <ul>
     *           <li><b>grantType</b>: The type of grant used for authentication.</li>
     *           <li><b>accessToken</b>: The access token for the session.</li>
     *           <li><b>refreshToken</b>: The refresh token to obtain a new access token.</li>
     *         </ul>
     */
    @PostMapping("/login")
    public ApiResult<JwtToken> login(@RequestBody LoginDTO loginDTO) {
        String memberId = loginDTO.getMemberId();
        String memberPw = loginDTO.getMemberPw();
        JwtToken jwtToken = authService.login(memberId, memberPw);
        refreshTokenService.saveRefreshToken(memberId, jwtToken.getRefreshToken());
        return ApiResult.<JwtToken>builder()
                .code(CODE_SUCCESS)
                .message("로그인 성공")
                .data(jwtToken)
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
    @PostMapping("/send-email-check-code")
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
                .code(CODE_SUCCESS)
                .message("이메일 전송 성공")
                .data(emailCheckCodeService.getEmailCheckCodeValidTimeInSeconds())
                .build();
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
    @PostMapping("/valid-email")
    public ApiResult<Boolean> validEmail(@RequestBody ValidEmailDTO validEmailDTO) {
        boolean isValidEmail = emailCheckCodeService.isCheckCodeCorrect(validEmailDTO.getEmail(), validEmailDTO.getCode());
        if(isValidEmail) {
            emailCheckCodeService.saveValidEmail(validEmailDTO.getEmail());
        }
        return ApiResult.<Boolean>builder()
                .code(CODE_SUCCESS)
                .message("이메일 체크 확인 성공")
                .data(isValidEmail)
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
     * logout 을 처리하는 API
     * logout 을 access token 을 redis black list 에 추가하는 방식으로 이루어진다.
     * TODO redis 를 이용한 logout 구현하기
     */
    @PatchMapping("/logout")
    public ApiResult<Void> logout(@RequestBody LogoutDTO logoutDTO) {
        refreshTokenService.deleteRefreshToken(logoutDTO.getRefreshToken());
        return ApiResult.<Void>builder()
                .code(CODE_SUCCESS)
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
    @PostMapping("/join")
    public ApiResult<JoinResultDTO> join(@RequestBody JoinDTO joinDTO) {
        JoinResultDTO joinResultDTO = authService.tryJoin(joinDTO, emailCheckCodeService.isValidEmail(joinDTO.getEmail()));
        return ApiResult.<JoinResultDTO>builder()
                .code(CODE_SUCCESS)
                .message("회원가입 시도 성공")
                .data(joinResultDTO)
                .build();
    }
}
