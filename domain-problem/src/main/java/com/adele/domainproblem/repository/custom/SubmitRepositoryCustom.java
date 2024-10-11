package com.adele.domainproblem.repository.custom;

import com.adele.domainproblem.dto.ReflectionNoteListDTO;
import com.adele.domainproblem.dto.SubmitStatisticsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SubmitRepositoryCustom {
    Page<ReflectionNoteListDTO> searchPage(Pageable pageable, String title, String langCode, String resultCode);

    SubmitStatisticsResponse getSubmitStatistics(String memberId);
}
