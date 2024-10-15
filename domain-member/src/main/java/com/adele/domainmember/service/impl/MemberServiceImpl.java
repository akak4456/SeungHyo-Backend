package com.adele.domainmember.service.impl;

import com.adele.domainmember.domain.Member;
import com.adele.domainmember.dto.*;
import com.adele.domainmember.repository.MemberRepository;
import com.adele.domainmember.service.MemberService;
import com.adele.domainredis.dto.JwtToken;
import com.adele.domainredis.jwt.JwtTokenProvider;
import com.adele.internalcommon.exception.business.member.*;
import com.adele.internalcommon.response.ErrorCode;
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
        Member member = memberRepository.findById(loginRequest.getMemberId()).orElse(null);
        if(member == null) {
            throw new CurrentPwNotMatchException(ErrorCode.CURRENT_PW_NOT_MATCH);
        }
        else if(member.isDeleteYn()) {
            throw new AlreadyWithdrawMemberException(ErrorCode.ALREADY_WITHDRAW_MEMBER_EXCEPTION);
        }
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
    // https://velog.io/@nestour95/Spring-Security-UserDetailsService%EC%97%90%EC%84%9C-%EB%B9%84%EB%B0%80%EB%B2%88%ED%98%B8%EB%8A%94-%EC%96%B4%EB%94%94%EC%97%90%EC%84%9C-%EA%B2%80%EC%82%AC%ED%95%98%EB%8A%94-%EA%B1%B8%EA%B9%8C 참고하기
    private UserDetails createUserDetails(Member member) {
        return User.builder()
                .username(member.getUsername())
                .password(member.getPassword())
                .roles(member.getRoles().toArray(new String[0]))
                .build();
    }

    @Override
    public void join(JoinRequest joinRequest) {
        if(joinRequest.getMemberPw() == null || !joinRequest.getMemberPw().equals(joinRequest.getMemberPwCheck())) {
            throw new PwAndPwCheckDoesNotSameException(ErrorCode.PW_AND_PW_CHECK_DOES_NOT_SAME);
        }
        if(memberRepository.findById(joinRequest.getMemberId()).isPresent()) {
            throw new IdDuplicateException(ErrorCode.ID_DUPLICATE);
        }
        if(memberRepository.findByEmail(joinRequest.getEmail()).isPresent()) {
            throw new EmailDuplicateException(ErrorCode.EMAIL_DUPLICATE);
        }
        Member member = new Member();
        member.setMemberId(joinRequest.getMemberId());
        member.setMemberPw(passwordEncoder.encode(joinRequest.getMemberPw()));
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
        if(member == null || dto.getMemberPw() == null ||!passwordEncoder.matches(dto.getMemberPw(), member.getMemberPw())) {
            throw new CurrentPwNotMatchException(ErrorCode.CURRENT_PW_NOT_MATCH);
        }
        member.setStatusMessage(dto.getStatusMessage());
    }

    @Override
    public void changePw(String memberId, ChangePwRequest request) {
        Member member = memberRepository.findById(memberId).orElse(null);
        if(member == null || request.getCurrentPw() == null || !passwordEncoder.matches(request.getCurrentPw(), member.getMemberPw())) {
            throw new CurrentPwNotMatchException(ErrorCode.CURRENT_PW_NOT_MATCH);
        }
        if(request.getCurrentPw().equals(request.getNewPw())) {
            throw new CurrentPwAndNewPwMatchException(ErrorCode.CURRENT_PW_AND_NEW_PW_MATCH_EXCEPTION);
        }
        if(request.getNewPw() == null || !request.getNewPw().equals(request.getNewPwCheck())) {
            throw new NewPwAndNewPwCheckDoesNotMatchException(ErrorCode.NEW_PW_AND_NEW_PW_CHECK_DOES_NOT_MATCH);
        }
        member.setMemberPw(passwordEncoder.encode(request.getNewPw()));
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
        jwtTokenProvider.validateRefreshToken(refreshToken);
        Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);
        return jwtTokenProvider.generateToken(authentication);
    }

    @Override
    public InfoResponse getInfo(String memberId) {
        InfoResponse result = new InfoResponse();
        Member member = memberRepository.findById(memberId).orElse(null);
        if(member != null) {
            result.setMemberId(member.getMemberId());
            result.setStatusMessage(member.getStatusMessage());
        }
        return result;
    }
}
