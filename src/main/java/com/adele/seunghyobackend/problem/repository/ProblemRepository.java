package com.adele.seunghyobackend.problem.repository;

import com.adele.seunghyobackend.problem.domain.Problem;
import com.adele.seunghyobackend.problem.repository.custom.ProblemRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemRepository extends JpaRepository<Problem, Long>, ProblemRepositoryCustom {
}
