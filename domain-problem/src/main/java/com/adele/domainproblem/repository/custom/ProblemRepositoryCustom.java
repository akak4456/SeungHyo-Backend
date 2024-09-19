package com.adele.domainproblem.repository.custom;

import com.adele.domainproblem.dto.ProblemGradeInfo;
import com.adele.domainproblem.dto.ProblemInfoInMainPage;
import com.adele.domainproblem.dto.ProblemListDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface ProblemRepositoryCustom {
    Page<ProblemListDTO> searchPage(Pageable pageable, String title);

    BigDecimal getCorrectionRatioById(Long id);

    ProblemInfoInMainPage getProblemInfoInMainPage();

    List<ProblemGradeInfo> getProblemGradeInfoList();
}
