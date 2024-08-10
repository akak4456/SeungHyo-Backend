package com.adele.memberservice.controller;

import com.adele.common.ApiResult;
import com.adele.common.AuthHeaderConstant;
import com.adele.common.ResponseCode;
import com.adele.memberservice.JwtTokenProvider;
import com.adele.memberservice.dto.*;
import com.adele.memberservice.service.EmailCheckCodeService;
import com.adele.memberservice.service.EmailService;
import com.adele.memberservice.service.MemberService;
import com.adele.memberservice.service.RefreshTokenService;
import com.adele.memberservice.service.impl.EmailCheckCodeServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
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
     *     <li><b>grantType</b> grant type</li>
     *     <li><b>accessToken</b> access token</li>
     *     <li><b>refreshToken</b> refresh token</li>
     * </ul>
     */
    @PostMapping("/auth/login")
    public ApiResult<JwtToken> login(@RequestBody LoginRequest loginRequest) {
        JwtToken response = memberService.login(loginRequest);
        refreshTokenService.saveRefreshToken(loginRequest.getMemberId(), response.getRefreshToken());
        log.info("response: {}", response);
        // TODO 회원탈퇴한 유저 같은 경우 로그인이 되지 않도록 변경하기
        ApiResult<JwtToken> res = ApiResult.<JwtToken>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .message("로그인 성공")
                .data(response)
                .build();
        return ApiResult.<JwtToken>builder()
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
    public ApiResult<InfoEditResultDTO> getInfoEdit(@RequestHeader(AuthHeaderConstant.AUTH_USER) String memberId) {
        InfoEditResultDTO result = memberService.getInfoEdit(memberId);
        return ApiResult.<InfoEditResultDTO>builder()
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
     * @return PatchInfoEditResultDTO
     * <ul>
     *     <li><b>idNotMatch</b> 입력한 id 랑 실제 아이디랑 다른지 여부</li>
     *     <li><b>idNotValidForm</b> id form 이 올바르지 않은지 여부</li>
     *     <li><b>statusMessageNotValidForm</b> status message form 이 올바르지 않은지 여부</li>
     *     <li><b>pwNotValidForm</b> 비밀번호가 올바르지 않은지 여부</li>
     *     <li><b>emailNotValidForm</b> 이메일이 올바르지 않은지 여부</li>
     *     <li><b>pwNotMatch</b> 입력한 pw 랑 실제 pw 랑 다른지 여부</li>
     * </ul>
     */
    @PatchMapping("/my/info-edit")
    public ApiResult<PatchInfoEditResultDTO> patchInfoEdit(@RequestHeader(AuthHeaderConstant.AUTH_USER) String memberId, @RequestBody PatchInfoEditDTO dto) {
        PatchInfoEditResultDTO result = memberService.patchInfoEdit(dto, memberId.equals(dto.getMemberId()));
        return ApiResult.<PatchInfoEditResultDTO>builder()
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
     *     <li><b>notExistUser</b> 존재하지 않는 유저로 시도하고자 하는 여부</li>
     *     <li><b>currentPwNotMatch</b> 실제 유저 비밀번호와 입력한 비밀번호가 다른지 여부</li>
     *     <li><b>currentPwAndNewPwMatch</b> 입력한 비밀번호와 새로 입력한 비밀번호가 같은지 여부</li>
     *     <li><b>newPwNotMatch</b> 새로 입력한 비밀번호와 비밀번호 확인이 같지 않은지 여부</li>
     *     <li><b>newPwNotValidForm</b> 새로 입력한 비밀번호의 폼이 맞지 않는지 여부</li>
     * </ul>
     */
    @PatchMapping("/my/change-pw")
    public ApiResult<ChangePwResultDTO> changePw(@RequestHeader(AuthHeaderConstant.AUTH_USER) String memberId, @RequestBody ChangePwDTO dto) {
        ChangePwResultDTO result = memberService.tryChangePw(memberId, dto);
        return ApiResult.<ChangePwResultDTO>builder()
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
