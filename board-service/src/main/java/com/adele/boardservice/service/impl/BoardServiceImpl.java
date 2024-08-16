package com.adele.boardservice.service.impl;

import com.adele.boardservice.domain.Board;
import com.adele.boardservice.domain.BoardCategory;
import com.adele.boardservice.dto.*;
import com.adele.boardservice.repository.BoardCategoryRepository;
import com.adele.boardservice.repository.BoardRepository;
import com.adele.boardservice.repository.ProblemClient;
import com.adele.boardservice.service.BoardService;
import com.adele.common.ApiResult;
import feign.Response;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;
    private final BoardCategoryRepository boardCategoryRepository;
    private final ProblemClient problemClient;

    @Override
    public Page<BoardListDTO> searchPage(BoardSearchCondition condition, Pageable pageable) {
        return boardRepository.searchPage(condition, pageable);
    }

    @Override
    public BoardOneDTO getOne(Long boardNo) {
        Board board = boardRepository.findById(boardNo).orElse(null);
        if(board == null) {
            return null;
        }
        return new BoardOneDTO(
                board.getBoardTitle(),
                board.getProblemNo(),
                board.getProblemTitle(),
                board.getMemberId(),
                board.getRegDate(),
                board.getLikeCount(),
                board.getBoardContent()
                );
    }

    @Override
    public List<BoardCategoryDTO> getCategories() {
        return boardCategoryRepository.findAll(Sort.by(Sort.Direction.ASC, "orderNum"))
                .stream()
                .filter((category) -> !category.isForAdmin())
                .map((category) ->
                        new BoardCategoryDTO(category.getCategoryCode(), category.getCategoryName())
                ).collect(Collectors.toList());
    }

    @Override
    public ProblemDTO getProblemOne(String problemNo) {
        ApiResult<ProblemDTO> body = problemClient.getProblemOne(problemNo).getBody();
        assert body != null;
        return body.getData();
    }

    @Override
    public void saveBoard(String memberId, BoardWriteDTO boardDTO, ProblemDTO problemDTO) {
        Board board = new Board();
        board.setMemberId(memberId);
        board.setBoardTitle(boardDTO.getBoardTitle());
        board.setBoardCategory(boardCategoryRepository.getReferenceById(boardDTO.getCategoryCode()));
        board.setLangCode(boardDTO.getLangCode());
        board.setLangName(boardDTO.getLangName());
        board.setProblemNo(Long.parseLong(boardDTO.getProblemNo()));
        board.setProblemTitle(problemDTO.getProblemTitle());
        board.setProblemTitle(board.getProblemTitle());
        board.setBoardContent(boardDTO.getNormalHTMLContent());
        board.setSourceCode(boardDTO.getSourceCode());
        boardRepository.save(board);
    }
}
