package com.adele.domainproblem.repository;

import com.adele.domainproblem.domain.ProblemGrade;
import com.adele.domainproblem.dto.KafkaCompile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProblemGradeRepository extends JpaRepository<ProblemGrade, Long> {
    @Query(value = """
        select new com.adele.domainproblem.dto.KafkaCompile(
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
