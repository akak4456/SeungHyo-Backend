package com.adele.boardservice.service.impl;

import com.adele.boardservice.domain.Board;
import com.adele.boardservice.dto.BoardListDTO;
import com.adele.boardservice.dto.BoardOneDTO;
import com.adele.boardservice.dto.BoardSearchCondition;
import com.adele.boardservice.repository.BoardRepository;
import com.adele.boardservice.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;

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
}
