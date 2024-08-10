package com.adele.problemservice.service.impl;

import com.adele.problemservice.dto.NewSubmitOneDTO;
import com.adele.problemservice.repository.ProgramLanguageRepository;
import com.adele.problemservice.service.ProgramLanguageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
}
