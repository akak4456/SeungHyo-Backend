package com.adele.domainboard.service.impl;

import com.adele.domainboard.dto.ReplyDTO;
import com.adele.domainboard.repository.ReplyRepository;
import com.adele.domainboard.service.ReplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReplyServiceImpl implements ReplyService {
    private final ReplyRepository replyRepository;
    @Override
    public Page<ReplyDTO> searchPage(Long boardNo, Pageable pageable) {
        return replyRepository.searchPage(boardNo, pageable);
    }
}
