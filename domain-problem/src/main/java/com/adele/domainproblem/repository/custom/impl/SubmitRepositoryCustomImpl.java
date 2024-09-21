package com.adele.domainproblem.repository.custom.impl;

import com.adele.domainproblem.SubmitStatus;
import com.adele.domainproblem.domain.QProblem;
import com.adele.domainproblem.domain.QProgramLanguage;
import com.adele.domainproblem.domain.QSubmitList;
import com.adele.domainproblem.dto.ReflectionNoteListDTO;
import com.adele.domainproblem.dto.SubmitCommitDTO;
import com.adele.domainproblem.dto.SubmitInYear;
import com.adele.domainproblem.dto.SubmitStatisticsResponse;
import com.adele.domainproblem.repository.custom.SubmitRepositoryCustom;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public SubmitStatisticsResponse getSubmitStatistics(String memberId) {
        QSubmitList submitList = QSubmitList.submitList;

        // 현재 년도를 구한다. 이렇게 하면 1월 1일이 return 된다.
        LocalDateTime startOfYear = LocalDateTime.now().withDayOfYear(1).withHour(0).withMinute(0).withSecond(0).withNano(0);

        // 올해 달마다 제출 대비 맞은 비율을 계산한다.
        List<SubmitInYear> ratioInCurrentYear = queryFactory
                .select(
                        submitList.submitDate.year(),
                        submitList.submitDate.month(),
                        submitList.count(),
                        submitList.submitResult
                                .when(SubmitStatus.CORRECT).then(1L)
                                .otherwise(0L).sum()
                )
                .from(submitList)
                .where(submitList.memberId.eq(memberId)
                        .and(submitList.submitDate.goe(startOfYear)))
                .groupBy(submitList.submitDate.year(), submitList.submitDate.month())
                .fetch().stream()
                .map(result -> {
                    // Extract year and month
                    Integer year = result.get(submitList.submitDate.year());
                    Integer month = result.get(submitList.submitDate.month());

                    // Extract total submissions and correct submissions for the current month
                    Long totalSubmissions = result.get(submitList.count());
                    Long correctSubmissions = result.get(submitList.submitResult
                            .when(SubmitStatus.CORRECT).then(1L)
                            .otherwise(0L).sum());

                    // Calculate the ratio of correct submissions
                    Double ratio = totalSubmissions != 0 ? (double) correctSubmissions / totalSubmissions : 0;

                    // Return a SubmitInYear object for each year/month combination
                    return new SubmitInYear(year, month, ratio);
                })
                .collect(Collectors.toList());

        // 맞은 것과 틀린 것의 비율을 구한다.
        Long rightCount = queryFactory.select(submitList.count())
                .from(submitList)
                .where(submitList.memberId.eq(memberId)
                        .and(submitList.submitResult.eq(SubmitStatus.CORRECT)))
                .fetchOne();

        Long wrongCount = queryFactory.select(submitList.count())
                .from(submitList)
                .where(submitList.memberId.eq(memberId)
                        .and(submitList.submitResult.ne(SubmitStatus.CORRECT)))
                .fetchOne();

        // 올해와 작년의 submit commit 를 얻어온다.
        List<SubmitCommitDTO> commits = queryFactory
                .select(Projections.constructor(SubmitCommitDTO.class,
                        submitList.submitDate,
                        submitList.count()
                ))
                .from(submitList)
                .where(submitList.memberId.eq(memberId)
                        .and(submitList.submitDate.goe(LocalDateTime.now().minusYears(1))))
                .groupBy(submitList.submitDate)
                .fetch();

        // 맞은 것과 틀린것의 번호를 얻어온다.
        List<Long> rightProblemNo = queryFactory.select(submitList.problem.problemNo)
                .distinct()
                .from(submitList)
                .where(submitList.memberId.eq(memberId)
                        .and(submitList.submitResult.eq(SubmitStatus.CORRECT)))
                .fetch();

        List<Long> wrongProblemNo = queryFactory.select(submitList.problem.problemNo)
                .distinct()
                .from(submitList)
                .where(submitList.memberId.eq(memberId))
                .groupBy(submitList.problem.problemNo)
                .having(
                        submitList.submitResult
                                .when(SubmitStatus.CORRECT).then(1L)
                                .otherwise(0L)
                                .sum().eq(0L)  // Only include problems with zero CORRECT submissions
                )
                .fetch();

        return new SubmitStatisticsResponse(
                ratioInCurrentYear,
                rightCount,
                wrongCount,
                commits,
                rightProblemNo,
                wrongProblemNo
        );
    }
}
