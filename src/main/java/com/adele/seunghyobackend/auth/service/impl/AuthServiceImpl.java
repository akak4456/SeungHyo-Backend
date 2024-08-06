package com.adele.seunghyobackend.auth.service.impl;

import com.adele.seunghyobackend.auth.dto.JoinDTO;
import com.adele.seunghyobackend.auth.dto.JoinResultDTO;
import com.adele.seunghyobackend.db.domain.Member;
import com.adele.seunghyobackend.security.model.dto.JwtToken;
import com.adele.seunghyobackend.db.repository.MemberRepository;
import com.adele.seunghyobackend.security.JwtTokenProvider;
import com.adele.seunghyobackend.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.adele.seunghyobackend.util.ValidFormUtil.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {
    private final MemberRepository memberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public JwtToken login(String memberId, String memberPw) {
        // 1. username + password 를 기반으로 Authentication 객체 생성
        // 이때 authentication 은 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(memberId, memberPw);
        log.info("authenticationToken: {}", authenticationToken);
        // 2. 실제 검증. authenticate() 메서드를 통해 요청된 Member 에 대한 검증 진행
        // authenticate 메서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

        return jwtToken;
    }

    @Override
    public JoinResultDTO tryJoin(JoinDTO joinDTO, boolean isEmailValid) {
        boolean joinAvailable = true;
        JoinResultDTO joinResultDTO = new JoinResultDTO();
        if(!validateIdForm(joinDTO.getMemberId())) {
            // 아이디가 올바른 형태가 아니냐?
            joinAvailable = false;
            joinResultDTO.setIdNotValidForm(true);
        }
        if(memberRepository.findById(joinDTO.getMemberId()).isPresent()) {
            // 아이디가 존재하냐?
            joinAvailable = false;
            joinResultDTO.setIdDuplicate(true);
        }
        if(!validateStatusMessageForm(joinDTO.getStatusMessage())) {
            // 상태 메시지가 올바른 형태가 아닌가?
            joinAvailable = false;
            joinResultDTO.setStatusNotValidForm(true);
        }
        if(!validatePwForm(joinDTO.getMemberPw())) {
            // 비밀번호가 올바른 형태가 아닌가?
            joinAvailable = false;
            joinResultDTO.setPwNotValidForm(true);
        }
        if(!joinDTO.getMemberPw().equals(joinDTO.getMemberPwCheck())) {
            // 비밀번호와 비밀번호 확인이 다르나?
            joinAvailable = false;
            joinResultDTO.setPwAndPwCheckDifferent(true);
        }
        if(!validateEmailForm(joinDTO.getEmail())) {
            // 이메일이 올바른 형태가 아닌가?
            joinAvailable = false;
            joinResultDTO.setEmailNotValidForm(true);
        }
        if(memberRepository.findByEmail(joinDTO.getEmail()).isPresent()) {
            // 이메일이 존재 하나?
            joinAvailable = false;
            joinResultDTO.setEmailDuplicate(true);
        }
        if(!isEmailValid) {
            // 이메일 인증을 안했나?
            joinAvailable = false;
            joinResultDTO.setEmailNotValidate(true);
        }
        if(joinAvailable) {
            Member member = new Member();
            member.setMemberId(joinDTO.getMemberId());
            member.setMemberPw(joinDTO.getMemberPw());
            member.setRoles(List.of("MEMBER"));
            member.setStatusMessage(joinDTO.getStatusMessage());
            member.setEmail(joinDTO.getEmail());
            memberRepository.save(member);
        }
        return joinResultDTO;
    }
}
