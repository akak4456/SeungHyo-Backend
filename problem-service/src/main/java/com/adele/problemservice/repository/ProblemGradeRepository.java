package com.adele.problemservice.repository;

import com.adele.problemservice.domain.ProblemGrade;
import com.adele.problemservice.dto.KafkaCompile;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProblemGradeRepository extends JpaRepository<ProblemGrade, Long> {
    @Query(value = """
        select new com.adele.problemservice.dto.KafkaCompile(
            p.gradeResult,
            p.gradeCaseNo,
            pIn.inputSource,
            pOut.outputSource,
            p.compileErrorReason,
            p.runtimeErrorReason
        )
        FROM ProblemGrade p
        LEFT JOIN p.input pIn
        LEFT JOIN p.output pOut
        WHERE p.submit.submitNo = :no
    """)
    List<KafkaCompile> findBySubmitNo(@Param("no") Long no);
}
