package com.adele.seunghyobackend.hello.controller;

import com.adele.seunghyobackend.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.adele.seunghyobackend.TestConstant.UNIT_TEST_TAG;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 컨트롤러 테스트는
 * https://velog.io/@wish17/JWT-JUnit-%ED%85%8C%EC%8A%A4%ED%8A%B8%EC%97%90%EC%84%9C-Authentication-%EA%B0%9D%EC%B2%B4-%EB%A7%8C%EB%93%A4%EC%96%B4-%EB%84%A3%EA%B8%B0
 * 를 참고해서 만들도록 한다.
 */
@WebMvcTest(controllers = HelloController.class)
@Import(TestConfig.class)
@Tag(UNIT_TEST_TAG)
public class HelloControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("hello string 을 반환하는지 확인해본다.")
    public void helloTest() throws Exception {
        ResultActions actions =
                mockMvc.perform(
                        get("/api/v1/hello")
                );

        actions.andExpect(status().isOk())
                .andExpect(content().string("hello"));
    }
}
