package com.adele.domainproblem.repository;


import com.adele.domainproblem.domain.SubmitList;
import com.adele.domainproblem.repository.custom.SubmitRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmitRepository extends JpaRepository<SubmitList, Long>, SubmitRepositoryCustom {
}
