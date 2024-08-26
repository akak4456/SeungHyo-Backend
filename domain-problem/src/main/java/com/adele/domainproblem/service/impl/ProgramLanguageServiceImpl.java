package com.adele.domainproblem.service.impl;

import com.adele.domainproblem.dto.NewSubmitOneDTO;
import com.adele.domainproblem.repository.ProgramLanguageRepository;
import com.adele.domainproblem.service.ProgramLanguageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProgramLanguageServiceImpl implements ProgramLanguageService {
    private final ProgramLanguageRepository programLanguageRepository;
    @Override
    public NewSubmitOneDTO findAll(Long problemNo) {
        return new NewSubmitOneDTO(programLanguageRepository.findByIdWithProblemNo(problemNo));
    }

    @Override
    public NewSubmitOneDTO findAll() {
        return new NewSubmitOneDTO(programLanguageRepository.findAll());
    }
}
