package com.adele.domainboard.service;

import com.adele.domainboard.dto.AddReplyRequest;
import com.adele.domainboard.dto.ReplyDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReplyService {
    /**
     * 리스트 조회 Service
     * @param pageable 페이지
     * @return Page 검색 결과
     */
    Page<ReplyDTO> searchPage(Long boardNo, Pageable pageable);

    void addReply(String memberId, Long boardNo, AddReplyRequest req);
}
