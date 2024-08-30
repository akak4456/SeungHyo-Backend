package com.adele.domainproblem.repository;

import com.adele.domainproblem.domain.*;
import com.adele.domainproblem.dto.ProblemGradeInfo;
import com.adele.domainproblem.repository.custom.ProblemRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProblemRepository extends JpaRepository<Problem, Long>, ProblemRepositoryCustom {

    @Query("SELECT t FROM Problem p " +
            "JOIN p.tagCorrelations tpc " +
            "JOIN tpc.tag t " +
            "WHERE p.problemNo = :id")
    List<ProblemTag> findByIdWithTag(@Param("id") Long id);

    @Query("SELECT a FROM Problem p " +
            "JOIN p.algorithmCategoryCorrelations ac " +
            "JOIN ac.algorithmCategory a " +
            "WHERE p.problemNo = :id")
    List<AlgorithmCategory> findByIdWithAlgorithmCategory(@Param("id") Long id);

    @Query("SELECT inputs.inputSource FROM Problem p " +
            "JOIN p.problemInputs inputs " +
            "WHERE p.problemNo = :id AND inputs.isExample = true")
    List<String> findByIdWithInputExampleOnly(@Param("id") Long id);

    @Query("SELECT outputs.outputSource FROM Problem p " +
            "JOIN p.problemOutputs outputs " +
            "WHERE p.problemNo = :id AND outputs.isExample = true")
    List<String> findByIdWithOutputExampleOnly(@Param("id") Long id);

    @Query("SELECT inputs FROM Problem p " +
            "JOIN p.problemInputs inputs " +
            "WHERE p.problemNo = :id")
    List<ProblemInput> findByIdWithInput(@Param("id") Long id);

    @Query("SELECT outputs FROM Problem p " +
            "JOIN p.problemOutputs outputs " +
            "WHERE p.problemNo = :id")
    List<ProblemOutput> findByIdWithOutput(@Param("id") Long id);

    @Query("SELECT con FROM Problem p " +
            "JOIN p.problemConditions con " +
            "WHERE p.problemNo = :id")
    List<ProblemCondition> findByIdWithCondition(@Param("id") Long id);
}
