package com.adele.domainproblem.repository.custom.impl;

import com.adele.domainproblem.SubmitStatus;
import com.adele.domainproblem.domain.QProblem;
import com.adele.domainproblem.domain.QProgramLanguage;
import com.adele.domainproblem.domain.QSubmitList;
import com.adele.domainproblem.dto.ProblemGradeInfo;
import com.adele.domainproblem.dto.ProblemInfoInMainPage;
import com.adele.domainproblem.dto.ProblemListDTO;
import com.adele.domainproblem.repository.custom.ProblemRepositoryCustom;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
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
    public Page<ProblemListDTO> searchPage(Pageable pageable, String title) {
        QProblem problem = QProblem.problem;
        QSubmitList submitList = QSubmitList.submitList;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(problem.isGradable);

        if(title != null && !title.isBlank()) {
            builder.and(problem.problemTitle.contains(title));
        }

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
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Count total problems for pagination
        JPQLQuery<Long> countQuery = queryFactory
                .select(problem.count())
                .from(problem)
                .where(builder);

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

    @Override
    public ProblemInfoInMainPage getProblemInfoInMainPage() {
        QProblem problem = QProblem.problem;
        QSubmitList submitList = QSubmitList.submitList;
        QProgramLanguage programLanguage = QProgramLanguage.programLanguage;
        return queryFactory
                .select(Projections.bean(
                        ProblemInfoInMainPage.class,
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(problem.problemNo.countDistinct())
                                        .from(problem),
                                "allProblemCount"
                        ),
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(problem.problemNo.countDistinct())
                                        .from(problem)
                                        .where(problem.isGradable),
                                "availableProblemCount"
                        ),
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(problem.problemNo.countDistinct())
                                        .from(problem)
                                        .join(submitList).on(submitList.problem.problemNo.eq(problem.problemNo))
                                        .where(submitList.submitResult.eq(SubmitStatus.CORRECT)),
                                "correctProblemCount"
                        ),
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(programLanguage.langCode.countDistinct())
                                        .from(programLanguage)
                                        .where(programLanguage.isGradable),
                                "availableLanguageCount"
                        )
                ))
                .from(problem)
                .fetchOne();
    }

    @Override
    public List<ProblemGradeInfo> getProblemGradeInfoList() {
        QProblem problem = QProblem.problem;
        QSubmitList submitList = QSubmitList.submitList;
        NumberPath<Long> aliasQuantity = Expressions.numberPath(Long.class, "submitCount");
        return queryFactory
                .select(Projections.bean(
                        ProblemGradeInfo.class,
                        problem.problemNo,
                        problem.problemTitle,
                        submitList.submitNo.countDistinct().as(aliasQuantity)
                ))
                .from(problem)
                .join(submitList).on(submitList.problem.problemNo.eq(problem.problemNo))
                .groupBy(problem.problemNo)
                .orderBy(aliasQuantity.desc())
                .offset(0)
                .limit(5)
                .fetch();
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
