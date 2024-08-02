package com.adele.seunghyobackend.controller;

import com.adele.seunghyobackend.model.dto.JwtToken;
import com.adele.seunghyobackend.model.dto.LoginDTO;
import com.adele.seunghyobackend.service.MemberService;
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
