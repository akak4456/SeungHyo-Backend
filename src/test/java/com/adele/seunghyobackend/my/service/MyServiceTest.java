package com.adele.seunghyobackend.my.service;

import com.adele.seunghyobackend.TestConfig;
import com.adele.seunghyobackend.auth.service.impl.AuthServiceImpl;
import com.adele.seunghyobackend.db.domain.Member;
import com.adele.seunghyobackend.db.repository.MemberRepository;
import com.adele.seunghyobackend.my.dto.InfoEditResultDTO;
import com.adele.seunghyobackend.my.dto.PatchInfoEditDTO;
import com.adele.seunghyobackend.my.dto.PatchInfoEditResultDTO;
import com.adele.seunghyobackend.my.service.impl.MyServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.Optional;

import static com.adele.seunghyobackend.TestConstant.UNIT_TEST_TAG;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(TestConfig.class)
@Slf4j
@Tag(UNIT_TEST_TAG)
public class MyServiceTest {
    @MockBean
    private MemberRepository memberRepository;
    private MyService myService;

    @BeforeEach
    public void setUp() {
        myService = new MyServiceImpl(memberRepository);
    }

    @Test
    @DisplayName("info edit 조회")
    public void getInfoEdit() {
        when(memberRepository.findById(anyString())).thenReturn(Optional.of(new Member(
                "user1",
                "pass1",
                "status1",
                "Y",
                "email1",
                List.of("ADMIN")
        )));
        InfoEditResultDTO result = myService.getInfoEdit("user1");
        assertThat(result).isNotNull();
        assertThat(result.getMemberId()).isEqualTo("user1");
        assertThat(result.getStatusMessage()).isEqualTo("status1");
        assertThat(result.getEmail()).isEqualTo("email1");
    }

    @Test
    @DisplayName("info edit 수정 성공")
    public void patchInfoEditSuccess() {
        when(memberRepository.findById(anyString())).thenReturn(Optional.of(new Member(
                "user1",
                "pass1",
                "status1",
                "Y",
                "email1",
                List.of("ADMIN")
        )));
        PatchInfoEditDTO dto = new PatchInfoEditDTO("user1", "pass1", "status1", "email1");
        PatchInfoEditResultDTO result = myService.patchInfoEdit(dto, true);
        assertThat(result.isIdNotMatch()).isFalse();
        assertThat(result.isIdNotValidForm()).isFalse();
        assertThat(result.isStatusMessageNotValidForm()).isFalse();
        assertThat(result.isPwNotValidForm()).isFalse();
        assertThat(result.isEmailNotValidForm()).isFalse();
        assertThat(result.isPwNotMatch()).isFalse();
    }

    @Test
    @DisplayName("info edit 수정 실패 - Id 폼이 잘못 됨")
    public void patchInfoEditFailCauseId() {
        when(memberRepository.findById(anyString())).thenReturn(Optional.of(new Member(
                "user1",
                "pass1",
                "status1",
                "Y",
                "email1",
                List.of("ADMIN")
        )));
        PatchInfoEditDTO dto = new PatchInfoEditDTO("", "pass1", "status1", "email1");
        PatchInfoEditResultDTO result = myService.patchInfoEdit(dto, true);
        assertThat(result.isIdNotMatch()).isFalse();
        assertThat(result.isIdNotValidForm()).isTrue();
        assertThat(result.isStatusMessageNotValidForm()).isFalse();
        assertThat(result.isPwNotValidForm()).isFalse();
        assertThat(result.isEmailNotValidForm()).isFalse();
        assertThat(result.isPwNotMatch()).isFalse();
    }

    @Test
    @DisplayName("info edit 수정 실패 - Pw 폼이 잘못 됨")
    public void patchInfoEditFailCausePw() {
        when(memberRepository.findById(anyString())).thenReturn(Optional.of(new Member(
                "user1",
                "pass1",
                "status1",
                "Y",
                "email1",
                List.of("ADMIN")
        )));
        PatchInfoEditDTO dto = new PatchInfoEditDTO("user1", "", "status1", "email1");
        PatchInfoEditResultDTO result = myService.patchInfoEdit(dto, true);
        assertThat(result.isIdNotMatch()).isFalse();
        assertThat(result.isIdNotValidForm()).isFalse();
        assertThat(result.isStatusMessageNotValidForm()).isFalse();
        assertThat(result.isPwNotValidForm()).isTrue();
        assertThat(result.isEmailNotValidForm()).isFalse();
        assertThat(result.isPwNotMatch()).isTrue();
    }

    @Test
    @DisplayName("info edit 수정 실패 - status message 폼이 잘못 됨")
    public void patchInfoEditFailCauseStatusMessage() {
        when(memberRepository.findById(anyString())).thenReturn(Optional.of(new Member(
                "user1",
                "pass1",
                "status1",
                "Y",
                "email1",
                List.of("ADMIN")
        )));
        PatchInfoEditDTO dto = new PatchInfoEditDTO("user1", "pass1", "", "email1");
        PatchInfoEditResultDTO result = myService.patchInfoEdit(dto, true);
        assertThat(result.isIdNotMatch()).isFalse();
        assertThat(result.isIdNotValidForm()).isFalse();
        assertThat(result.isStatusMessageNotValidForm()).isTrue();
        assertThat(result.isPwNotValidForm()).isFalse();
        assertThat(result.isEmailNotValidForm()).isFalse();
        assertThat(result.isPwNotMatch()).isFalse();
    }

    @Test
    @DisplayName("info edit 수정 실패 - email 폼이 잘못 됨")
    public void patchInfoEditFailCauseEmail() {
        when(memberRepository.findById(anyString())).thenReturn(Optional.of(new Member(
                "user1",
                "pass1",
                "status1",
                "Y",
                "email1",
                List.of("ADMIN")
        )));
        PatchInfoEditDTO dto = new PatchInfoEditDTO("user1", "pass1", "status1", "");
        PatchInfoEditResultDTO result = myService.patchInfoEdit(dto, true);
        assertThat(result.isIdNotMatch()).isFalse();
        assertThat(result.isIdNotValidForm()).isFalse();
        assertThat(result.isStatusMessageNotValidForm()).isFalse();
        assertThat(result.isPwNotValidForm()).isFalse();
        assertThat(result.isEmailNotValidForm()).isTrue();
        assertThat(result.isPwNotMatch()).isFalse();
    }

    @Test
    @DisplayName("info edit 수정 실패 - 입력한 비밀번호와 실제 비밀번호가 다를때")
    public void patchInfoEditFailCauseMember() {
        when(memberRepository.findById(anyString())).thenReturn(Optional.of(new Member(
                "user1",
                "pass1",
                "status1",
                "Y",
                "email1",
                List.of("ADMIN")
        )));
        PatchInfoEditDTO dto = new PatchInfoEditDTO("user1", "pass2", "status1", "email1");
        PatchInfoEditResultDTO result = myService.patchInfoEdit(dto, true);
        assertThat(result.isIdNotMatch()).isFalse();
        assertThat(result.isIdNotValidForm()).isFalse();
        assertThat(result.isStatusMessageNotValidForm()).isFalse();
        assertThat(result.isPwNotValidForm()).isFalse();
        assertThat(result.isEmailNotValidForm()).isFalse();
        assertThat(result.isPwNotMatch()).isTrue();
    }
    
    @Test
    @DisplayName("info edit 수정 실패 - id match 하지 않음")
    public void patchInfoEditFailCauseIdNotMatch() {
        when(memberRepository.findById(anyString())).thenReturn(Optional.of(new Member(
                "user1",
                "pass1",
                "status1",
                "Y",
                "email1",
                List.of("ADMIN")
        )));
        PatchInfoEditDTO dto = new PatchInfoEditDTO("user1", "pass1", "status1", "email1");
        PatchInfoEditResultDTO result = myService.patchInfoEdit(dto, false);
        assertThat(result.isIdNotMatch()).isTrue();
        assertThat(result.isIdNotValidForm()).isFalse();
        assertThat(result.isStatusMessageNotValidForm()).isFalse();
        assertThat(result.isPwNotValidForm()).isFalse();
        assertThat(result.isEmailNotValidForm()).isFalse();
        assertThat(result.isPwNotMatch()).isFalse();
    }
}
