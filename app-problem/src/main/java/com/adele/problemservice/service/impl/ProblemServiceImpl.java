package com.adele.problemservice.service.impl;

import com.adele.problemservice.domain.Problem;
import com.adele.problemservice.dto.ProblemListDTO;
import com.adele.problemservice.dto.ProblemOneDTO;
import com.adele.problemservice.repository.ProblemRepository;
import com.adele.problemservice.service.ProblemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
