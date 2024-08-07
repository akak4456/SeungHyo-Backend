package com.adele.seunghyobackend.programlanguage.repository;

import com.adele.seunghyobackend.programlanguage.domain.ProgramLanguage;
import io.lettuce.core.dynamic.annotation.Param;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProgramLanguageRepository extends JpaRepository<ProgramLanguage, Long> {
    @Query("SELECT l FROM ProgramLanguage l " +
            "JOIN l.correlationList lc " +
            "WHERE lc.problem.problemNo = :id")
    List<ProgramLanguage> findByIdWithProblemNo(@NotNull @Param("id") Long id);
}
