package com.adele.seunghyobackend.problem.service;

import com.adele.seunghyobackend.problem.dto.ProblemListDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProblemService {
    /**
     * problem list 조회 서비스
     * @param pageable 조회할 페이지
     * @return Page&lt;ProblemListDTO&gt; 페이지 객체
     */
    Page<ProblemListDTO> searchPage(Pageable pageable);
}
