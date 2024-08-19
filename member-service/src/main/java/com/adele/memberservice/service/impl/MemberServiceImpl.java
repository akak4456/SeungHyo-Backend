package com.adele.memberservice.service.impl;

import com.adele.memberservice.JwtTokenProvider;
import com.adele.memberservice.domain.Member;
import com.adele.memberservice.dto.*;
import com.adele.memberservice.repository.MemberRepository;
import com.adele.memberservice.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    @Override
    public JwtToken login(LoginRequest loginRequest) {
        // 1. username + password 를 기반으로 Authentication 객체 생성
        // 이때 authentication 은 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getMemberId(), loginRequest.getMemberPw());
        log.info("authenticationToken: {}", authenticationToken);
        // 2. 실제 검증. authenticate() 메서드를 통해 요청된 Member 에 대한 검증 진행
        // authenticate 메서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        log.info("authentication: {}", authentication);
        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        return jwtTokenProvider.generateToken(authentication);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails userDetails = memberRepository.findById(username)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다."));
        log.info("userDetails {}", userDetails);
        return userDetails;
    }

    // 해당하는 User 의 데이터가 존재한다면 UserDetails 객체로 만들어서 return
    private UserDetails createUserDetails(Member member) {
        return User.builder()
                .username(member.getUsername())
                .password(passwordEncoder.encode(member.getPassword()))
                .roles(member.getRoles().toArray(new String[0]))
                .build();
    }

    @Override
    public void join(JoinRequest joinRequest) {
        Member member = new Member();
        member.setMemberId(joinRequest.getMemberId());
        member.setMemberPw(joinRequest.getMemberPw());
        member.setRoles(List.of("MEMBER"));
        member.setStatusMessage(joinRequest.getStatusMessage());
        member.setEmail(joinRequest.getEmail());
        memberRepository.save(member);
    }

    @Override
    public boolean isIdExist(String id) {
        return memberRepository.findById(id).isPresent();
    }

    @Override
    public boolean isEmailExist(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }

    @Override
    public GetInfoEditResponse getInfoEdit(String memberId) {
        GetInfoEditResponse result = new GetInfoEditResponse();
        Member member = memberRepository.findById(memberId).orElse(null);
        if (member != null) {
            result.setMemberId(member.getMemberId());
            result.setStatusMessage(member.getStatusMessage());
            result.setEmail(member.getEmail());
        }
        return result;
    }

    @Override
    public void patchInfoEdit(PatchInfoEditRequest dto) {
        Member member = memberRepository.findById(dto.getMemberId()).orElse(null);
        assert member != null;
        member.setStatusMessage(dto.getStatusMessage());
    }

    @Override
    public boolean isPwMatch(String id, String pw) {
        Member member = memberRepository.findById(id).orElse(null);
        return member != null && member.getMemberPw().equals(pw);
    }

    @Override
    public void changePw(String memberId, String memberPw) {
        Member member = memberRepository.findById(memberId).orElse(null);
        assert member != null;
        member.setMemberPw(memberPw);
    }

    @Override
    public boolean withdraw(String memberId) {
        Member member = memberRepository.findById(memberId).orElse(null);
        if(member == null) {
            return false;
        }
        member.setDeleteYn(true);
        return true;
    }

    @Override
    public JwtToken reissue(String refreshToken) {
        if(!jwtTokenProvider.refreshTokenValidation(refreshToken)) {
            throw new IllegalArgumentException("refresh token is not valid");
        }
        Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);
        return jwtTokenProvider.generateToken(authentication);
    }
}
