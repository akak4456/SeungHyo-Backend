package com.adele.seunghyobackend.my.service;

import com.adele.seunghyobackend.TestConfig;
import com.adele.seunghyobackend.auth.service.impl.AuthServiceImpl;
import com.adele.seunghyobackend.db.domain.Member;
import com.adele.seunghyobackend.db.repository.MemberRepository;
import com.adele.seunghyobackend.my.dto.*;
import com.adele.seunghyobackend.my.service.impl.MyServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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
                false,
                "email1",
                List.of("ADMIN")
        )));
        InfoEditResultDTO result = myService.getInfoEdit("user1");
        assertThat(result).isNotNull();
        assertThat(result.getMemberId()).isEqualTo("user1");
        assertThat(result.getStatusMessage()).isEqualTo("status1");
        assertThat(result.getEmail()).isEqualTo("email1");
    }

    @ParameterizedTest
    @DisplayName("info-edit 수정 테스트")
    @MethodSource("provideInfoEdit")
    public void patchInfoEdit(
            PatchInfoEditDTO dto,
            boolean idMatch,
            PatchInfoEditResultDTO expected) {
        when(memberRepository.findById(anyString())).thenReturn(Optional.of(new Member(
                "user1",
                "pass1",
                "status1",
                false,
                "email1",
                List.of("ADMIN")
        )));
        PatchInfoEditResultDTO result = myService.patchInfoEdit(dto, idMatch);
        assertThat(result).isEqualTo(expected);
    }

    private static Stream<Arguments> provideInfoEdit() {
        return Stream.of(
                Arguments.of(
                new PatchInfoEditDTO("user1", "pass1", "status1", "email1"), true,
                new PatchInfoEditResultDTO(false, false, false, false, false, false)
                ),
                Arguments.of(
                        new PatchInfoEditDTO("", "pass1", "status1", "email1"), true,
                        new PatchInfoEditResultDTO(false, true, false, false, false, false)
                ),
                Arguments.of(
                        new PatchInfoEditDTO("user1", "", "status1", "email1"), true,
                        new PatchInfoEditResultDTO(false, false, false, true, false, true)
                ),
                Arguments.of(
                        new PatchInfoEditDTO("user1", "pass1", "", "email1"), true,
                        new PatchInfoEditResultDTO(false, false, true, false, false, false)
                ),
                Arguments.of(
                        new PatchInfoEditDTO("user1", "pass1", "status1", ""), true,
                        new PatchInfoEditResultDTO(false, false, false, false, true, false)
                ),
                Arguments.of(
                        new PatchInfoEditDTO("user1", "pass2", "status1", "email1"), true,
                        new PatchInfoEditResultDTO(false, false, false, false, false, true)
                ),
                Arguments.of(
                        new PatchInfoEditDTO("user1", "pass1", "status1", "email1"), false,
                        new PatchInfoEditResultDTO(true, false, false, false, false, false)
                )
        );
    }

    @ParameterizedTest
    @DisplayName("비밀번호 수정 테스트")
    @MethodSource("provideChangePw")
    public void changePw(
            String memberId,
            ChangePwDTO dto,
            ChangePwResultDTO expected
    ) {
        when(memberRepository.findById("user1")).thenReturn(Optional.of(new Member(
                "user1",
                "pass1",
                "status1",
                false,
                "email1",
                List.of("ADMIN")
        )));
        ChangePwResultDTO result = myService.tryChangePw(memberId, dto);
        assertThat(result).isEqualTo(expected);
    }

    private static Stream<Arguments> provideChangePw() {
        return Stream.of(
                Arguments.of(
                        "user1",
                        new ChangePwDTO("pass1","pass2","pass2"),
                        new ChangePwResultDTO(false, false, false, false, false)
                ),
                Arguments.of(
                        "user2",
                        new ChangePwDTO("pass1","pass2","pass2"),
                        new ChangePwResultDTO(true, false, false, false, false)
                ),
                Arguments.of(
                        "user1",
                        new ChangePwDTO("pass3","pass2","pass2"),
                        new ChangePwResultDTO(false, true, false, false, false)
                ),
                Arguments.of(
                        "user1",
                        new ChangePwDTO("","pass2","pass2"),
                        new ChangePwResultDTO(false, true, false, false, false)
                ),
                Arguments.of(
                        "user1",
                        new ChangePwDTO("pass1","pass1","pass1"),
                        new ChangePwResultDTO(false, false, true, false, false)
                ),
                Arguments.of(
                        "user1",
                        new ChangePwDTO("pass1","pass2","pass3"),
                        new ChangePwResultDTO(false, false, false, true, false)
                ),
                Arguments.of(
                        "user1",
                        new ChangePwDTO("pass1","",""),
                        new ChangePwResultDTO(false, false, false, false, true)
                )
        );
    }

}
