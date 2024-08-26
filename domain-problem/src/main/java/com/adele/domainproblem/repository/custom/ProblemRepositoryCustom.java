package com.adele.domainproblem.repository.custom;

import com.adele.domainproblem.dto.ProblemListDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface ProblemRepositoryCustom {
    Page<ProblemListDTO> searchPage(Pageable pageable);

    BigDecimal getCorrectionRatioById(Long id);
}
