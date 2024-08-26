package com.adele.domainproblem.repository;

import com.adele.domainproblem.domain.ProgramLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProgramLanguageRepository extends JpaRepository<ProgramLanguage, String> {
    @Query("SELECT l FROM ProgramLanguage l " +
            "JOIN l.correlationList lc " +
            "WHERE lc.problem.problemNo = :id")
    List<ProgramLanguage> findByIdWithProblemNo(@Param("id") Long id);
}
