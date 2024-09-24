package com.adele.domainboard.repository;

import com.adele.domainboard.domain.Board;
import com.adele.domainboard.domain.BoardLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {
    BoardLike findByBoardAndMemberId(Board board, String memberId);
}
