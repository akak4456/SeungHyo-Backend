package com.adele.domainboard.repository;

import com.adele.domainboard.domain.BoardCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardCategoryRepository extends JpaRepository<BoardCategory, String> {
}
