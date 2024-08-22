package com.adele.problemservice.repository;

import com.adele.problemservice.domain.ProgramLanguage;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProgramLanguageRepository extends JpaRepository<ProgramLanguage, String> {
    @Query("SELECT l FROM ProgramLanguage l " +
            "JOIN l.correlationList lc " +
            "WHERE lc.problem.problemNo = :id")
    List<ProgramLanguage> findByIdWithProblemNo(@Param("id") Long id);
}
