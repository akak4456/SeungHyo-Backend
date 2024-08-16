package com.adele.boardservice.controller;

import com.adele.boardservice.dto.*;
import com.adele.boardservice.repository.BoardRepository;
import com.adele.boardservice.repository.ProblemClient;
import com.adele.boardservice.service.BoardService;
import com.adele.common.ApiResult;
import com.adele.common.AuthHeaderConstant;
import com.adele.common.ResponseCode;
import feign.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;

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

    /**
     * 게시글 조회를 한다. 여기에서 댓글은 조회하지 않으며
     * 댓글 같은 경우 별도의 paging api 를 이용하도록 한다.
     * @param boardNo 게시글 번호
     * @return BoardOneDTO 게시글
     * <ul>
     *     <li><b>boardTitle</b> 게시글 제목</li>
     *     <li><b>problemNo</b> 문제 번호</li>
     *     <li><b>problemTitle</b> 문제 제목</li>
     *     <li><b>boardMemberId</b> 글 작성자</li>
     *     <li><b>boardRegDate</b> 등록일</li>
     *     <li><b>boardLikeCount</b> 좋아요 수</li>
     *     <li><b>boardContent</b> 글 내용</li>
     * </ul>
     */
    @GetMapping("{boardNo}")
    public ApiResult<BoardOneDTO> getOne(
            @PathVariable("boardNo") Long boardNo
    ) {
        BoardOneDTO boardOneDTO = boardService.getOne(boardNo);
        return ApiResult.<BoardOneDTO>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .message("하나 조회 성공")
                .data(boardOneDTO)
                .build();
    }

    /**
     * 게시판 카테고리 종류를 얻어온다
     * @return BoardCategoryResponse 카테고리 종류 응답
     */
    @GetMapping("/categories")
    public ApiResult<BoardCategoryResponse> getCategories() {
        return ApiResult.<BoardCategoryResponse>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .message("카테고리 조회 성공")
                .data(new BoardCategoryResponse(boardService.getCategories()))
                .build();
    }

    /**
     * 게시판 글쓰기 추가한다. form 도 확인하도록 한다.
     * @param memberId 글쓰기한 사람 id
     * @param board 글쓰기한 form data
     * <ul>
     *     <li><b>boardTitle</b> 게시글 제목</li>
     *     <li><b>categoryCode</b> 카테고리 코드</li>
     *     <li><b>categoryName</b> 카테고리 이름</li>
     *     <li><b>langCode</b> 언어 코드</li>
     *     <li><b>langName</b> 언어명</li>
     *     <li><b>problemNo</b> 문제 번호</li>
     *     <li><b>normalHTMLContent</b> 일반 글 내용</li>
     *     <li><b>sourceCode</b> 소스 코드 글 내용</li>
     * </ul>
     * @param errors 글쓰기한 form error 들
     * @return BoardWriteResultDTO
     * <ul>
     *     <li><b>boardTitleError</b> 글 제목에 문제가 있는지 없으면 빈칸, 있으면 문제 코드</li>
     *     <li><b>categoryCodeError</b> 카테고리 코드에 문제가 있는지 없으면 빈칸, 있으면 문제 코드</li>
     *     <li><b>categoryNameError</b> 카테고리 이름에 문제가 있는지 없으면 빈칸, 있으면 문제 코드</li>
     *     <li><b>langCodeError</b> 언어 코드에 문제가 있는지 없으면 빈칸, 있으면 문제 코드</li>
     *     <li><b>langNameError</b> 언어명에 문제가 있는지 없으면 빈칸 있으면 문제코드</li>
     *     <li><b>problemNoError</b> 문제 번호에 문제가 있는지 없으면 빈칸 있으면 문제 코드</li>
     *     <li><b>normalHTMLContentError</b> 일반 글 내용에 문제가 있는지 없으면 빈칸 있으면 문제 코드</li>
     *     <li><b>sourceCodeError</b> 소스 코드 글 내용에 문제가 있는지 없으면 빈칸 있으면 문제 코드</li>
     * </ul>
     */
    @PostMapping("")
    public ApiResult<BoardWriteResultDTO> addBoard(@RequestHeader(AuthHeaderConstant.AUTH_USER) String memberId, @RequestBody @Valid BoardWriteDTO board, Errors errors){
        BoardWriteResultDTO result = new BoardWriteResultDTO();
        if(errors.hasErrors()) {
            for(FieldError error : errors.getFieldErrors()) {
                String fieldName = error.getField();
                String setterName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1) + "Error";
                try {
                    Method setter = BoardWriteResultDTO.class.getMethod(setterName, String.class);
                    setter.invoke(result, error.getDefaultMessage());
                } catch (Exception e) {
                    log.error("error occur but not handle because this error is tiny", e);
                }
            }
        } else {
            ProblemDTO problemResponse = boardService.getProblemOne(board.getProblemNo());
            if(problemResponse.getProblemTitle() == null) {
                result.setIsProblemNoValid(false);
            } else {
                result.setIsProblemNoValid(true);
                boardService.saveBoard(memberId, board, problemResponse);
            }
        }
        return ApiResult.<BoardWriteResultDTO>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .message("게시판 추가 성공")
                .data(result)
                .build();
    }
}
