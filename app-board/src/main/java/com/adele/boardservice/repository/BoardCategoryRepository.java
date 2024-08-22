package com.adele.boardservice.repository;

import com.adele.boardservice.domain.BoardCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardCategoryRepository extends JpaRepository<BoardCategory, String> {
}
