package com.adele.boardservice.repository.custom;

import com.adele.boardservice.dto.BoardListDTO;
import com.adele.boardservice.dto.BoardSearchCondition;
import com.adele.boardservice.dto.ReplyDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReplyRepositoryCustom {
    Page<ReplyDTO> searchPage(Long boardNo, Pageable pageable);
}
