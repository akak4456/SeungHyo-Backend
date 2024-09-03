package com.adele.domainboard.repository.custom.impl;

import com.adele.domainboard.domain.Board;
import com.adele.domainboard.domain.QBoard;
import com.adele.domainboard.domain.QBoardCategory;
import com.adele.domainboard.domain.QReply;
import com.adele.domainboard.dto.BoardInfoDTO;
import com.adele.domainboard.dto.BoardInfoInMain;
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

import java.time.LocalDateTime;
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

    @Override
    public BoardInfoDTO getBoardInfoInMainPage() {
        QBoard board = QBoard.board;
        QBoardCategory boardCategory = QBoardCategory.boardCategory;
        QReply reply = QReply.reply; // QReply 객체 추가

        // 1. 새로운 글: regDate가 가장 최근인 것 5개
        List<BoardInfoInMain> newBoard = queryFactory
                .select(Projections.constructor(BoardInfoInMain.class,
                        board.boardNo,
                        board.memberId,
                        board.boardTitle,
                        board.regDate,
                        board.likeCount,
                        JPAExpressions
                                .select(reply.count()) // replyCount 계산
                                .from(reply)
                                .where(reply.board.eq(board))
                ))
                .from(board)
                .orderBy(board.regDate.desc())
                .limit(5)
                .fetch();

        // 2. 인기 글: regDate가 한 달 이내인 글 중 likeCount가 가장 높은 5개
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        List<BoardInfoInMain> popularBoard = queryFactory
                .select(Projections.constructor(BoardInfoInMain.class,
                        board.boardNo,
                        board.memberId,
                        board.boardTitle,
                        board.regDate,
                        board.likeCount,
                        JPAExpressions
                                .select(reply.count()) // replyCount 계산
                                .from(reply)
                                .where(reply.board.eq(board))
                ))
                .from(board)
                .where(board.regDate.after(oneMonthAgo))
                .orderBy(board.likeCount.desc())
                .limit(5)
                .fetch();

        // 3. 공지 글: boardCategory.categoryCode가 'NOTICE'인 것 중에서 regDate가 가장 최근인 것 5개
        List<BoardInfoInMain> noticeBoard = queryFactory
                .select(Projections.constructor(BoardInfoInMain.class,
                        board.boardNo,
                        board.memberId,
                        board.boardTitle,
                        board.regDate,
                        board.likeCount,
                        JPAExpressions
                                .select(reply.count()) // replyCount 계산
                                .from(reply)
                                .where(reply.board.eq(board))
                ))
                .from(board)
                .join(board.boardCategory, boardCategory)
                .where(boardCategory.categoryCode.eq("NOTICE"))
                .orderBy(board.regDate.desc())
                .limit(5)
                .fetch();

        // 결과를 BoardInfoDTO에 담아서 반환
        return new BoardInfoDTO(newBoard, popularBoard, noticeBoard);
    }
}
