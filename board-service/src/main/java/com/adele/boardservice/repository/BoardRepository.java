package com.adele.boardservice.repository;

import com.adele.boardservice.domain.Board;
import com.adele.boardservice.repository.custom.BoardRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardRepositoryCustom {
}
