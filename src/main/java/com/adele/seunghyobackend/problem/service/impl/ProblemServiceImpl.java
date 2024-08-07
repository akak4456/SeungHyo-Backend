package com.adele.seunghyobackend.problem.service.impl;

import com.adele.seunghyobackend.problem.dto.ProblemListDTO;
import com.adele.seunghyobackend.problem.service.ProblemService;
import com.adele.seunghyobackend.problem.repository.ProblemRepository;
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
}
