package com.adele.problemservice.service.impl;

import com.adele.problemservice.SourceCodeDisclosureScope;
import com.adele.problemservice.SubmitStatus;
import com.adele.problemservice.domain.Problem;
import com.adele.problemservice.domain.ProgramLanguage;
import com.adele.problemservice.domain.SubmitList;
import com.adele.problemservice.dto.NewSubmitRequestDTO;
import com.adele.problemservice.dto.NewSubmitResultDTO;
import com.adele.problemservice.repository.ProblemRepository;
import com.adele.problemservice.repository.ProgramLanguageRepository;
import com.adele.problemservice.repository.SubmitRepository;
import com.adele.problemservice.service.SubmitService;
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
    private final ProblemRepository problemRepository;
    private final ProgramLanguageRepository programLanguageRepository;
    @Override
    public NewSubmitResultDTO tryNewSubmit(String memberId, NewSubmitRequestDTO newSubmitRequestDTO) {
        Problem problem = problemRepository.getReferenceById(newSubmitRequestDTO.getProblemNo());
        ProgramLanguage language = programLanguageRepository.getReferenceById(newSubmitRequestDTO.getLangCode());
        SubmitList submit = SubmitList.builder()
                .memberId(memberId)
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
