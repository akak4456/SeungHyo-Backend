package com.adele.domainboard.repository;

import com.adele.domainboard.domain.Reply;
import com.adele.domainboard.domain.ReplyLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyLikeRepository extends JpaRepository<ReplyLike, Long> {
    ReplyLike findByReplyAndMemberId(Reply reply, String memberId);
}
