package com.adele.seunghyobackend.member.controller;

import com.adele.seunghyobackend.common.ApiResult;
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

    /**
     * login 을 처리하는 API
     * login 성공 시 JWT 토큰을 반환
     * login 실패 시
     * @param loginDTO
     * @return
     */
    @PostMapping("/login")
    public ApiResult<JwtToken> login(@RequestBody LoginDTO loginDTO) {
        String memberId = loginDTO.getMemberId();
        String memberPw = loginDTO.getMemberPw();
        JwtToken jwtToken = memberService.login(memberId, memberPw);
        return ApiResult.<JwtToken>builder()
                .code(CODE_SUCCESS)
                .message("로그인 성공")
                .data(jwtToken)
                .build();
    }
}
