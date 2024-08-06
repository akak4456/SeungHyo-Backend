package com.adele.seunghyobackend.my.service.impl;

import com.adele.seunghyobackend.db.domain.Member;
import com.adele.seunghyobackend.db.repository.MemberRepository;
import com.adele.seunghyobackend.my.dto.InfoEditResultDTO;
import com.adele.seunghyobackend.my.dto.PatchInfoEditDTO;
import com.adele.seunghyobackend.my.dto.PatchInfoEditResultDTO;
import com.adele.seunghyobackend.my.service.MyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.adele.seunghyobackend.util.ValidFormUtil.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MyServiceImpl implements MyService {

    private final MemberRepository memberRepository;

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
}
