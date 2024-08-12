package com.adele.problemservice.repository;

import com.adele.problemservice.domain.ProblemGrade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemGradeRepository extends JpaRepository<ProblemGrade, Long> {
}
