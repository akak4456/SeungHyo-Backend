package com.adele.domainproblem.repository.custom;

import com.adele.domainproblem.dto.ReflectionNoteListDTO;
import com.adele.domainproblem.dto.SubmitStatisticsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SubmitRepositoryCustom {
    Page<ReflectionNoteListDTO> searchPage(Pageable pageable);

    SubmitStatisticsResponse getSubmitStatistics(String memberId);
}
