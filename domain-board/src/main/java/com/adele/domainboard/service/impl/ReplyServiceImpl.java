package com.adele.domainboard.service.impl;

import com.adele.domainboard.domain.Board;
import com.adele.domainboard.domain.Reply;
import com.adele.domainboard.domain.ReplyLike;
import com.adele.domainboard.dto.AddReplyRequest;
import com.adele.domainboard.dto.ReplyDTO;
import com.adele.domainboard.repository.BoardRepository;
import com.adele.domainboard.repository.ReplyLikeRepository;
import com.adele.domainboard.repository.ReplyRepository;
import com.adele.domainboard.service.ReplyService;
import com.adele.internalcommon.exception.business.member.LikeDuplicateException;
import com.adele.internalcommon.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReplyServiceImpl implements ReplyService {
    private final BoardRepository boardRepository;
    private final ReplyRepository replyRepository;
    private final ReplyLikeRepository replyLikeRepository;
    @Override
    public Page<ReplyDTO> searchPage(Long boardNo, Pageable pageable) {
        return replyRepository.searchPage(boardNo, pageable);
    }

    @Override
    public void addReply(String memberId, Long boardNo, AddReplyRequest req) {
        Board board = boardRepository.getReferenceById(boardNo);
        Reply reply = new Reply();
        reply.setReplyContent(req.getContent());
        reply.setBoard(board);
        reply.setLikeCount(0L);
        reply.setMemberId(memberId);
        reply.setSourceCode(req.getSourceContent());
        reply.setRegDate(LocalDateTime.now());
        reply.setLangCode(req.getLangCode());
        reply.setLangName(req.getLangName());
        replyRepository.save(reply);
    }

    @Override
    public void addReplyLike(Long replyNo, String memberId) {
        Reply reply = replyRepository.findById(replyNo).orElse(null);
        if(replyLikeRepository.findByReplyAndMemberId(reply, memberId) != null) {
            throw new LikeDuplicateException(ErrorCode.LIKE_DUPLICATE);
        }
        assert reply != null;
        reply.setLikeCount(reply.getLikeCount() + 1);
        ReplyLike replyLike = new ReplyLike();
        replyLike.setReply(reply);
        replyLike.setMemberId(memberId);
        replyLikeRepository.save(replyLike);
    }
}
