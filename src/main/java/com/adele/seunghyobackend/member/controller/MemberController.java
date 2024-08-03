package com.adele.seunghyobackend.member.controller;

import com.adele.seunghyobackend.security.model.dto.JwtToken;
import com.adele.seunghyobackend.member.model.dto.LoginDTO;
import com.adele.seunghyobackend.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/login")
    public JwtToken login(@RequestBody LoginDTO loginDTO) {
        String memberId = loginDTO.getMemberId();
        String memberPw = loginDTO.getMemberPw();
        JwtToken jwtToken = memberService.login(memberId, memberPw);
        return jwtToken;
    }
}
