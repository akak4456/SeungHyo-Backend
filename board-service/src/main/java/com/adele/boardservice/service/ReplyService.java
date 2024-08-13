package com.adele.boardservice.service;

import com.adele.boardservice.dto.BoardListDTO;
import com.adele.boardservice.dto.BoardSearchCondition;
import com.adele.boardservice.dto.ReplyDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReplyService {
    /**
     * 리스트 조회 Service
     * @param pageable 페이지
     * @return Page 검색 결과
     */
    Page<ReplyDTO> searchPage(Long boardNo, Pageable pageable);
}
