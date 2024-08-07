package com.adele.seunghyobackend.problem.repository.custom.impl;

import com.adele.seunghyobackend.problem.domain.QProblem;
import com.adele.seunghyobackend.problem.dto.ProblemListDTO;
import com.adele.seunghyobackend.problem.repository.custom.ProblemRepositoryCustom;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

@RequiredArgsConstructor
public class ProblemRepositoryCustomImpl implements ProblemRepositoryCustom {
    private final JPAQueryFactory queryFactory;


    @Override
    public Page<ProblemListDTO> searchPage(Pageable pageable) {
        QProblem problem = QProblem.problem;
        List<ProblemListDTO> fetch = queryFactory
                .select(Projections.bean(ProblemListDTO.class,
                        problem.problemNo,
                        problem.problemTitle))
                .from(problem)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        // TODO 올바른 값으로 채워넣기
        JPQLQuery<Long> countQuery = queryFactory
                .select(problem.count())
                .from(problem);

        return PageableExecutionUtils.getPage(fetch, pageable, countQuery::fetchCount);
    }
}
