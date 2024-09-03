package com.adele.domainboard.service;

import com.adele.domainboard.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

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

    /**
     * 게시글 카테고리 조회 서비스
     * @return List BoardCategoryDTO 게시판 카테고리들 단 admin을 위한 것은 얻어오지 않는다.
     */
    List<BoardCategoryDTO> getCategories();

    /**
     * problem 을 얻어오는 서비스
     * @param problemNo 문제 번호
     * @return Response
     */
    ProblemDTO getProblemOne(String problemNo);

    /**
     * 게시판 저장 서비스
     * @param memberId 게시판 등록자 아이디
     * @param boardDTO board
     * @param problemDTO problem
     */
    void saveBoard(String memberId, BoardWriteDTO boardDTO, ProblemDTO problemDTO);

    /**
     * 메인화면에 필요한 board 관련 정보를 얻어오는 서비스
     */
    BoardInfoDTO getBoardInfoInMainPage();
}
