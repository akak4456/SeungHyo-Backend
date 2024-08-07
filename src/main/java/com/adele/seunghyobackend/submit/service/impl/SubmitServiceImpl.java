package com.adele.seunghyobackend.submit.service.impl;

import com.adele.seunghyobackend.data.SourceCodeDisclosureScope;
import com.adele.seunghyobackend.data.SubmitStatus;
import com.adele.seunghyobackend.member.domain.Member;
import com.adele.seunghyobackend.member.repository.MemberRepository;
import com.adele.seunghyobackend.problem.domain.Problem;
import com.adele.seunghyobackend.problem.repository.ProblemRepository;
import com.adele.seunghyobackend.programlanguage.domain.ProgramLanguage;
import com.adele.seunghyobackend.programlanguage.repository.ProgramLanguageRepository;
import com.adele.seunghyobackend.submit.domain.SubmitList;
import com.adele.seunghyobackend.submit.dto.NewSubmitRequestDTO;
import com.adele.seunghyobackend.submit.dto.NewSubmitResultDTO;
import com.adele.seunghyobackend.submit.repository.SubmitRepository;
import com.adele.seunghyobackend.submit.service.SubmitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SubmitServiceImpl implements SubmitService {
    private final SubmitRepository submitRepository;
    private final MemberRepository memberRepository;
    private final ProblemRepository problemRepository;
    private final ProgramLanguageRepository programLanguageRepository;
    @Override
    public NewSubmitResultDTO tryNewSubmit(String memberId, NewSubmitRequestDTO newSubmitRequestDTO) {
        Member member = memberRepository.getReferenceById(memberId);
        Problem problem = problemRepository.getReferenceById(newSubmitRequestDTO.getProblemNo());
        ProgramLanguage language = programLanguageRepository.getReferenceById(newSubmitRequestDTO.getLangCode());
        SubmitList submit = SubmitList.builder()
                .member(member)
                .problem(problem)
                .submitResult(SubmitStatus.WAIT)
                .maxMemory(BigDecimal.ZERO)
                .maxTime(BigDecimal.ZERO)
                .language(language)
                .openRange(SourceCodeDisclosureScope.valueOf(newSubmitRequestDTO.getSourceCodeDisclosureScope()))
                .sourceCode(newSubmitRequestDTO.getSourceCode())
                .build();
        submitRepository.save(submit);
        return new NewSubmitResultDTO(true);
    }
}
