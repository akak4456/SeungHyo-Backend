package com.adele.domainboard.repository.custom.impl;

import com.adele.domainboard.domain.QBoard;
import com.adele.domainboard.domain.QBoardCategory;
import com.adele.domainboard.domain.QReply;
import com.adele.domainboard.dto.BoardListDTO;
import com.adele.domainboard.dto.BoardSearchCondition;
import com.adele.domainboard.repository.custom.BoardRepositoryCustom;
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

/**
 * Querydsl config 가 필요함
 */
@RequiredArgsConstructor
public class BoardRepositoryCustomImpl implements BoardRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    @Override
    public Page<BoardListDTO> searchPage(BoardSearchCondition condition, Pageable pageable) {
        QBoard board = QBoard.board;
        QReply reply = QReply.reply;
        QBoardCategory boardCategory = QBoardCategory.boardCategory;

        BooleanBuilder whereClause = new BooleanBuilder();
        if (condition.getCategoryCode() != null && !"ALL".equals(condition.getCategoryCode())) {
            whereClause.and(board.boardCategory.categoryCode.eq(condition.getCategoryCode()));
        }

        // Fetch problem list with correct people count, submit count, and correct ratio
        List<BoardListDTO> fetch = queryFactory
                .select(Projections.bean(
                        BoardListDTO.class,
                        board.boardNo,
                        board.boardTitle,
                        board.boardCategory.categoryCode,
                        board.boardCategory.categoryName,
                        board.langName,
                        board.memberId,
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(reply.countDistinct())
                                        .from(reply)
                                        .where(reply.board.eq(board)),
                                "replyCount"
                        ),
                        board.likeCount,
                        board.regDate
                ))
                .from(board)
                .where(whereClause)
                .orderBy(board.boardNo.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Count total problems for pagination
        JPQLQuery<Long> countQuery = queryFactory
                .select(board.count())
                .from(board)
                .where(whereClause);

        return PageableExecutionUtils.getPage(fetch, pageable, countQuery::fetchCount);
    }
}
