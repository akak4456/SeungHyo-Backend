package com.adele.seunghyobackend.problem.repository;

import com.adele.seunghyobackend.problem.domain.*;
import com.adele.seunghyobackend.problem.repository.custom.ProblemRepositoryCustom;
import io.lettuce.core.dynamic.annotation.Param;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProblemRepository extends JpaRepository<Problem, Long>, ProblemRepositoryCustom {

    @Query("SELECT t FROM Problem p " +
            "JOIN p.tagCorrelations tpc " +
            "JOIN tpc.tag t " +
            "WHERE p.problemNo = :id")
    List<ProblemTag> findByIdWithTag(@NotNull @Param("id") Long id);

    @Query("SELECT a FROM Problem p " +
            "JOIN p.algorithmCategoryCorrelations ac " +
            "JOIN ac.algorithmCategory a " +
            "WHERE p.problemNo = :id")
    List<AlgorithmCategory> findByIdWithAlgorithmCategory(@NotNull @Param("id") Long id);

    @Query("SELECT l FROM Problem p " +
            "JOIN p.programLanguageCorrelations lc " +
            "JOIN lc.programLanguage l " +
            "WHERE p.problemNo = :id")
    List<ProgramLanguage> findByIdWithLanguage(@NotNull @Param("id") Long id);

    @Query("SELECT inputs.inputSource FROM Problem p " +
            "JOIN p.problemInputs inputs " +
            "WHERE p.problemNo = :id")
    List<String> findByIdWithInput(@NotNull @Param("id") Long id);

    @Query("SELECT outputs.outputSource FROM Problem p " +
            "JOIN p.problemOutputs outputs " +
            "WHERE p.problemNo = :id")
    List<String> findByIdWithOutput(@NotNull @Param("id") Long id);

    @Query("SELECT con FROM Problem p " +
            "JOIN p.problemConditions con " +
            "WHERE p.problemNo = :id")
    List<ProblemCondition> findByIdWithCondition(@NotNull @Param("id") Long id);
}
