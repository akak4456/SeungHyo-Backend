package com.adele.boardservice.repository.custom.impl;

import com.adele.boardservice.domain.QBoard;
import com.adele.boardservice.domain.QBoardCategory;
import com.adele.boardservice.domain.QReply;
import com.adele.boardservice.dto.BoardListDTO;
import com.adele.boardservice.dto.ReplyDTO;
import com.adele.boardservice.repository.custom.ReplyRepositoryCustom;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

@RequiredArgsConstructor
public class ReplyRepositoryCustomImpl implements ReplyRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    @Override
    public Page<ReplyDTO> searchPage(Long boardNo, Pageable pageable) {
        QReply reply = QReply.reply;

        // Fetch problem list with correct people count, submit count, and correct ratio
        List<ReplyDTO> fetch = queryFactory
                .select(Projections.bean(
                        ReplyDTO.class,
                        reply.replyNo,
                        reply.memberId,
                        reply.regDate,
                        reply.likeCount,
                        reply.replyContent
                ))
                .from(reply)
                .where(reply.board.boardNo.eq(boardNo))
                .orderBy(reply.replyNo.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Count total problems for pagination
        JPQLQuery<Long> countQuery = queryFactory
                .select(reply.count())
                .from(reply)
                .where(reply.board.boardNo.eq(boardNo));

        return PageableExecutionUtils.getPage(fetch, pageable, countQuery::fetchCount);
    }
}
