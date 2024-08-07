package com.adele.seunghyobackend.submit.repository;

import com.adele.seunghyobackend.submit.domain.SubmitList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmitRepository extends JpaRepository<SubmitList, Long> {
}
