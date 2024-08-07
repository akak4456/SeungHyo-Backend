package com.adele.seunghyobackend.programlanguage.service.impl;

import com.adele.seunghyobackend.programlanguage.domain.ProgramLanguage;
import com.adele.seunghyobackend.programlanguage.dto.NewSubmitOneDTO;
import com.adele.seunghyobackend.programlanguage.repository.ProgramLanguageRepository;
import com.adele.seunghyobackend.programlanguage.service.ProgramLanguageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProgramLanguageServiceImpl implements ProgramLanguageService {
    @Autowired
    private final ProgramLanguageRepository programLanguageRepository;
    @Override
    public NewSubmitOneDTO findAll(Long problemNo) {
        return new NewSubmitOneDTO(programLanguageRepository.findByIdWithProblemNo(problemNo));
    }
}
