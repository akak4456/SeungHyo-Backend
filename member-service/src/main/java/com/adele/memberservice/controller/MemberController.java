package com.adele.memberservice.controller;

import com.adele.common.ApiResult;
import com.adele.common.ResponseCode;
import com.adele.memberservice.dto.LoginRequest;
import com.adele.memberservice.dto.LoginResponse;
import com.adele.memberservice.service.MemberService;
import com.adele.memberservice.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {
    private final MemberService memberService;
    private final RefreshTokenService refreshTokenService;
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
     * 정보 수정에서 이용할 데이터를 조회한다
     * @return InfoEditResultDTO
     * <ul>
     *     <li><b>memberId</b> 조회한 member id</li>
     *     <li><b>statusMessage</b> 조회한 상태 메시지</li>
     *     <li><b>email</b> 조회한 이메일</li>
     * </ul>
     */
    @GetMapping("/my/info-edit")
    public String getInfoEdit() {
        return "hello";
    }
}
