package com.adele.domainboard.repository.custom;

import com.adele.domainboard.dto.ReplyDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReplyRepositoryCustom {
    Page<ReplyDTO> searchPage(Long boardNo, Pageable pageable);
}
