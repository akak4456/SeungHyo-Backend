package com.adele.seunghyobackend.member.controller;

import com.adele.seunghyobackend.common.ApiResult;
import com.adele.seunghyobackend.member.model.dto.LogoutDTO;
import com.adele.seunghyobackend.member.service.impl.RefreshTokenService;
import com.adele.seunghyobackend.security.JwtTokenProvider;
import com.adele.seunghyobackend.security.model.dto.JwtToken;
import com.adele.seunghyobackend.member.model.dto.LoginDTO;
import com.adele.seunghyobackend.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.adele.seunghyobackend.common.Constant.CODE_SUCCESS;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

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
        JwtToken jwtToken = memberService.login(memberId, memberPw);
        refreshTokenService.saveRefreshToken(memberId, jwtToken.getRefreshToken());
        return ApiResult.<JwtToken>builder()
                .code(CODE_SUCCESS)
                .message("로그인 성공")
                .data(jwtToken)
                .build();
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
}
