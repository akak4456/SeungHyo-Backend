package com.adele.domainboard.repository.custom;

import com.adele.domainboard.dto.BoardListDTO;
import com.adele.domainboard.dto.BoardSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardRepositoryCustom {
    Page<BoardListDTO> searchPage(BoardSearchCondition condition, Pageable pageable);
}
