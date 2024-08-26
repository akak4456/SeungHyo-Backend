package com.adele.domainproblem.repository.custom.impl;

import com.adele.domainproblem.SubmitStatus;
import com.adele.domainproblem.domain.QProblem;
import com.adele.domainproblem.domain.QSubmitList;
import com.adele.domainproblem.dto.ProblemListDTO;
import com.adele.domainproblem.repository.custom.ProblemRepositoryCustom;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
public class ProblemRepositoryCustomImpl implements ProblemRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ProblemListDTO> searchPage(Pageable pageable) {
        QProblem problem = QProblem.problem;
        QSubmitList submitList = QSubmitList.submitList;

        // Fetch problem list with correct people count, submit count, and correct ratio
        List<ProblemListDTO> fetch = queryFactory
                .select(Projections.bean(
                        ProblemListDTO.class,
                        problem.problemNo,
                        problem.problemTitle,
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(submitList.memberId.countDistinct())
                                        .from(submitList)
                                        .where(submitList.problem.eq(problem).and(submitList.submitResult.eq(SubmitStatus.CORRECT))),
                                "correctPeopleCount"
                        ),
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(submitList.count())
                                        .from(submitList)
                                        .where(submitList.problem.eq(problem)),
                                "submitCount"
                        ),
                        correctionExpression(problem, submitList)
                ))
                .from(problem)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Count total problems for pagination
        JPQLQuery<Long> countQuery = queryFactory
                .select(problem.count())
                .from(problem);

        return PageableExecutionUtils.getPage(fetch, pageable, countQuery::fetchCount);
    }

    @Override
    public BigDecimal getCorrectionRatioById(Long id) {
        QProblem problem = QProblem.problem;
        QSubmitList submitList = QSubmitList.submitList;
        return queryFactory
                .select(correctionExpression(problem, submitList))
                .from(problem)
                .where(problem.problemNo.eq(id))
                .fetchOne();
    }

    private Expression<BigDecimal> correctionExpression(QProblem problem, QSubmitList submitList) {
        return ExpressionUtils.as(
                Expressions.numberTemplate(BigDecimal.class, "COALESCE(ROUND((cast({0} as double) / nullif(cast({1} as double), 0)), 5), 0)", // Explicitly casting for division
                        JPAExpressions
                                .select(submitList.count())
                                .from(submitList)
                                .where(submitList.problem.eq(problem).and(submitList.submitResult.eq(SubmitStatus.CORRECT))),
                        JPAExpressions
                                .select(submitList.count())
                                .from(submitList)
                                .where(submitList.problem.eq(problem))
                ),
                "correctRatio"
        );
    }
}
