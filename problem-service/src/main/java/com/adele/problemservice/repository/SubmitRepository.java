package com.adele.problemservice.repository;


import com.adele.problemservice.domain.SubmitList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmitRepository extends JpaRepository<SubmitList, Long> {
}
