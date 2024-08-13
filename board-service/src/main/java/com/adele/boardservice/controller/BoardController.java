package com.adele.boardservice.controller;

import com.adele.boardservice.dto.BoardListDTO;
import com.adele.boardservice.dto.BoardSearchCondition;
import com.adele.boardservice.service.BoardService;
import com.adele.common.ApiResult;
import com.adele.common.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/board")
@RequiredArgsConstructor
@Slf4j
public class BoardController {
    private final BoardService boardService;

    /**
     * board list 를 조회한다.
     * @param pageable
     * page: 조회할 page number. 0부터 시작한다
     * size: 한 페이지당 들어갈 content 갯수
     * @return Page&lt;ProblemListDTO&gt;
     * <ul>
     *     <li><b>totalElements<b/> 조회된 elements 수</li>
     *     <li><b>totalPages</b> 조회된 총 page 숫자</li>
     *     <li>
     *         <p>content(BoardListDTO)</p>
     *         <ul>
     *             <li><b>boardNo</b> 게시판 번호</li>
     *             <li><b>boardTitle</b> 게시판 제목</li>
     *             <li><b>categoryCode</b> 게시판 카테고리 코드</li>
     *             <li><b>categoryName</b> 게시판 카테고리</li>
     *             <li><b>langName</b> 언어</li>
     *             <li><b>memberId</b> 글쓴이</li>
     *             <li><b>replyCount</b> 댓글 갯수/li>
     *             <li><b>likeCount</b> 좋아요 개수</li>
     *             <li><b>regDate</b> 등록 일자</li>
     *         </ul>
     *     </li>
     * </ul>
     */
    @GetMapping({""})
    public ApiResult<Page<BoardListDTO>> getSearch(
            @ModelAttribute BoardSearchCondition condition,
            @PageableDefault
            Pageable pageable
    ) {
        Page<BoardListDTO> page = boardService.searchPage(condition, pageable);
        return ApiResult.<Page<BoardListDTO>>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .message("리스트 조회 성공")
                .data(page)
                .build();
    }
}
