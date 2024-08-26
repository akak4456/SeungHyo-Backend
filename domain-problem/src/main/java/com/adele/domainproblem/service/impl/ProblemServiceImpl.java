package com.adele.domainproblem.service.impl;

import com.adele.domainproblem.domain.Problem;
import com.adele.domainproblem.domain.ProblemCondition;
import com.adele.domainproblem.dto.ConditionDTO;
import com.adele.domainproblem.dto.ProblemListDTO;
import com.adele.domainproblem.dto.ProblemOneDTO;
import com.adele.domainproblem.repository.ProblemRepository;
import com.adele.domainproblem.service.ProblemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProblemServiceImpl implements ProblemService {
    private final ProblemRepository problemRepository;
    @Override
    public Page<ProblemListDTO> searchPage(Pageable pageable) {
        return problemRepository.searchPage(pageable);
    }

    @Override
    public ProblemOneDTO problemOne(Long problemNo) {
        Problem problem = problemRepository.findById(problemNo).orElse(null);
        ProblemOneDTO problemOneDTO = new ProblemOneDTO();
        if(problem != null) {
            problemOneDTO.setProblemTitle(problem.getProblemTitle());
            problemOneDTO.setProblemTags(problemRepository.findByIdWithTag(problemNo));
            problemOneDTO.setProblemCondition(problemRepository.findByIdWithCondition(problemNo));
            problemOneDTO.setCorrectRatio(problemRepository.getCorrectionRatioById(problemNo));
            problemOneDTO.setProblemExplain(problem.getProblemExplain());
            problemOneDTO.setProblemInputExplain(problem.getProblemInputExplain());
            problemOneDTO.setProblemOutputExplain(problem.getProblemOutputExplain());
            problemOneDTO.setProblemInput(problemRepository.findByIdWithInputExampleOnly(problemNo));
            problemOneDTO.setProblemOutput(problemRepository.findByIdWithOutputExampleOnly(problemNo));
            problemOneDTO.setAlgorithmCategory(problemRepository.findByIdWithAlgorithmCategory(problemNo));
        }
        return problemOneDTO;
    }

    @Override
    public ConditionDTO getCondition(Long problemNo, String langCode) {
        List<ProblemCondition> conditions = problemRepository.findByIdWithCondition(problemNo);
        ProblemCondition condition = null;
        for (ProblemCondition c : conditions) {
            if(c.getLanguage().getLangCode().equals(langCode)) {
                condition = c;
                break;
            }
        }
        return new ConditionDTO(
                problemRepository.findByIdWithInput(problemNo),
                problemRepository.findByIdWithOutput(problemNo),
                condition.getConditionTime(),
                condition.getConditionMemory()
        );
    }
}
