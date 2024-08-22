package com.adele.problemservice.repository.custom.impl;

import com.adele.problemservice.SubmitStatus;
import com.adele.problemservice.domain.QProblem;
import com.adele.problemservice.domain.QProgramLanguage;
import com.adele.problemservice.domain.QSubmitList;
import com.adele.problemservice.dto.ProblemListDTO;
import com.adele.problemservice.dto.ReflectionNoteListDTO;
import com.adele.problemservice.repository.custom.ProblemRepositoryCustom;
import com.adele.problemservice.repository.custom.SubmitRepositoryCustom;
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
public class SubmitRepositoryCustomImpl implements SubmitRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ReflectionNoteListDTO> searchPage(Pageable pageable) {
        QSubmitList submitList = QSubmitList.submitList;
        QProblem problem = QProblem.problem;
        QProgramLanguage language = QProgramLanguage.programLanguage;

        // Fetch problem list with correct people count, submit count, and correct ratio
        List<ReflectionNoteListDTO> fetch = queryFactory
                .select(Projections.bean(
                        ReflectionNoteListDTO.class,
                        submitList.submitNo,
                        problem.problemTitle,
                        submitList.submitResult,
                        language.langName,
                        submitList.submitDate
                ))
                .from(submitList)
                .join(submitList.problem, problem)
                .join(submitList.language, language)
                .orderBy(submitList.submitNo.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Count total problems for pagination
        JPQLQuery<Long> countQuery = queryFactory
                .select(submitList.count())
                .from(submitList);

        return PageableExecutionUtils.getPage(fetch, pageable, countQuery::fetchCount);
    }
}
