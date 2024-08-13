package com.adele.boardservice.repository.custom;

import com.adele.boardservice.dto.BoardListDTO;
import com.adele.boardservice.dto.BoardSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardRepositoryCustom {
    Page<BoardListDTO> searchPage(BoardSearchCondition condition, Pageable pageable);
}
