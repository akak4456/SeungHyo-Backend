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

import static com.adele.common.ValidFormUtil.*;

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
    public LoginResponse login(LoginRequest loginRequest) {
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

    @Override
    public InfoEditResultDTO getInfoEdit(String memberId) {
        InfoEditResultDTO result = new InfoEditResultDTO();
        Member member = memberRepository.findById(memberId).orElse(null);
        if (member != null) {
            result.setMemberId(member.getMemberId());
            result.setStatusMessage(member.getStatusMessage());
            result.setEmail(member.getEmail());
        }
        return result;
    }

    @Override
    public PatchInfoEditResultDTO patchInfoEdit(PatchInfoEditDTO dto, boolean idMatch) {
        PatchInfoEditResultDTO result = new PatchInfoEditResultDTO();
        boolean available = true;
        if(!idMatch) {
            available = false;
            result.setIdNotMatch(true);
        }
        if(!validateIdForm(dto.getMemberId())) {
            available = false;
            result.setIdNotValidForm(true);
        }
        if(!validatePwForm(dto.getMemberPw())) {
            available = false;
            result.setPwNotValidForm(true);
        }
        if(!validateEmailForm(dto.getEmail())) {
            available = false;
            result.setEmailNotValidForm(true);
        }
        if(!validateStatusMessageForm(dto.getStatusMessage())) {
            available = false;
            result.setStatusMessageNotValidForm(true);
        }
        Member member = memberRepository.findById(dto.getMemberId()).orElse(null);
        if(member == null || !member.getMemberPw().equals(dto.getMemberPw())) {
            available = false;
            result.setPwNotMatch(true);
        }
        if(available) {
            member.setStatusMessage(dto.getStatusMessage());
        }
        return result;
    }

    @Override
    public ChangePwResultDTO tryChangePw(String memberId, ChangePwDTO dto) {
        ChangePwResultDTO result = new ChangePwResultDTO();
        boolean available = true;
        Member member = memberRepository.findById(memberId).orElse(null);
        if(member == null) {
            available = false;
            result.setNotExistUser(true);
        } else {
            if (member.getMemberPw() == null || !member.getMemberPw().equals(dto.getCurrentPw())) {
                available = false;
                result.setCurrentPwNotMatch(true);
            }
            if (dto.getNewPw() == null || dto.getNewPw().equals(dto.getCurrentPw())) {
                available = false;
                result.setCurrentPwAndNewPwMatch(true);
            }
            if(dto.getNewPw() == null || !dto.getNewPw().equals(dto.getNewPwCheck())) {
                available = false;
                result.setNewPwNotMatch(true);
            }
            if(!validatePwForm(dto.getNewPw())) {
                available = false;
                result.setNewPwNotValidForm(true);
            }
        }
        if(available) {
            member.setMemberPw(dto.getNewPw());
        }
        return result;
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
}
