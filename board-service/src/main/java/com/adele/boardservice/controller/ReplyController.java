package com.adele.boardservice.controller;

import com.adele.boardservice.dto.BoardListDTO;
import com.adele.boardservice.dto.BoardSearchCondition;
import com.adele.boardservice.dto.ReplyDTO;
import com.adele.boardservice.service.ReplyService;
import com.adele.common.ApiResult;
import com.adele.common.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reply")
@RequiredArgsConstructor
@Slf4j
public class ReplyController {
    private final ReplyService replyService;
    /**
     * reply list 를 조회한다.
     * @param pageable
     * page: 조회할 page number. 0부터 시작한다
     * size: 한 페이지당 들어갈 content 갯수
     * @return Page&lt;ProblemListDTO&gt;
     * <ul>
     *     <li><b>totalElements<b/> 조회된 elements 수</li>
     *     <li><b>totalPages</b> 조회된 총 page 숫자</li>
     *     <li>
     *         <p>content(ReplyDTO)</p>
     *         <ul>
     *             <li><b>replyNo</b> 답글 번호</li>
     *             <li><b>memberId</b> 답글 작성자</li>
     *             <li><b>regDate</b> 답글 등록일</li>
     *             <li><b>likeCount</b> 답글 좋아요 개수</li>
     *             <li><b>replyContent</b> 답글 내용</li>
     *         </ul>
     *     </li>
     * </ul>
     */
    @GetMapping({"{boardNo}"})
    public ApiResult<Page<ReplyDTO>> getSearch(
            @PathVariable Long boardNo,
            @PageableDefault
            Pageable pageable
    ) {
        Page<ReplyDTO> page = replyService.searchPage(boardNo, pageable);
        return ApiResult.<Page<ReplyDTO>>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .message("리스트 조회 성공")
                .data(page)
                .build();
    }
}
