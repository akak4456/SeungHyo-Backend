package com.adele.domainboard.repository;

import com.adele.domainboard.domain.Board;
import com.adele.domainboard.repository.custom.BoardRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardRepositoryCustom {
}
