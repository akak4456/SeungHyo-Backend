package com.adele.boardservice.service;

import com.adele.boardservice.DotenvTestExecutionListener;
import com.adele.boardservice.domain.Board;
import com.adele.boardservice.dto.BoardListDTO;
import com.adele.boardservice.dto.BoardSearchCondition;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

@SpringBootTest
@TestExecutionListeners(listeners = {
        DotenvTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class
})
@ActiveProfiles("dev")
@Slf4j
public class ServiceIntegratedTest {
    @Autowired
    private BoardService boardService;

    @Test
    @DisplayName("page 가 정상 작동하는지 확인해본다.")
    public void pageTest() {
        BoardSearchCondition condition = new BoardSearchCondition();
        condition.setCategoryCode("NOTICE");
        Pageable pageable = PageRequest.of(2,10);
        Page<BoardListDTO> result = boardService.searchPage(condition, pageable);
        for (BoardListDTO problem : result.getContent()) {
            log.info(problem.toString());
        }
    }
}
