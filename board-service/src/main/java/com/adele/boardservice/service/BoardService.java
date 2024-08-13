package com.adele.boardservice.service;

import com.adele.boardservice.dto.BoardListDTO;
import com.adele.boardservice.dto.BoardOneDTO;
import com.adele.boardservice.dto.BoardSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardService {
    /**
     * 리스트 조회 Service
     * @param condition 검색 조건
     * @param pageable 페이지
     * @return Page 검색 결과
     */
    Page<BoardListDTO> searchPage(BoardSearchCondition condition, Pageable pageable);

    /**
     * 게시글 조회 Service
     * @param boardNo 게시판 번호
     * @return BoardOneDTO 게시글
     */
    BoardOneDTO getOne(Long boardNo);
}
