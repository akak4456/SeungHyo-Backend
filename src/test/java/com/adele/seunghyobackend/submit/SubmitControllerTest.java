package com.adele.seunghyobackend.submit;

import com.adele.seunghyobackend.TestConfig;
import com.adele.seunghyobackend.member.my.dto.PatchInfoEditDTO;
import com.adele.seunghyobackend.programlanguage.controller.ProgramLanguageController;
import com.adele.seunghyobackend.submit.controller.SubmitController;
import com.adele.seunghyobackend.submit.dto.NewSubmitRequestDTO;
import com.adele.seunghyobackend.submit.service.SubmitService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.adele.seunghyobackend.TestConstant.UNIT_TEST_TAG;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SubmitController.class)
@SpringJUnitConfig(TestConfig.class)
@Slf4j
@Tag(UNIT_TEST_TAG)
public class SubmitControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Gson gson;

    @MockBean
    private SubmitService submitService;

    @Test
    @DisplayName("새로운 submit이 정상 실행되는지 확인한다.")
    @WithMockUser(username="user1", password="pass1")
    public void newSubmit() throws Exception {
        NewSubmitRequestDTO dto = new NewSubmitRequestDTO(1L,"JAVA_11", "ALL", "sourcecode");
        String content = gson.toJson(dto);
        ResultActions actions =
                mockMvc.perform(
                        post("/api/v1/submit")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                );

        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"));
    }
}
