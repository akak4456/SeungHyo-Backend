package com.adele.problemservice.service;

import com.adele.problemservice.CompileStatus;
import com.adele.problemservice.SubmitStatus;
import com.adele.problemservice.TestConfig;
import com.adele.problemservice.domain.SubmitList;
import com.adele.problemservice.dto.CompileResultDTO;
import com.adele.problemservice.dto.UpdateSubmitResponse;
import com.adele.problemservice.repository.SubmitRepository;
import com.adele.problemservice.service.impl.SubmitServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(TestConfig.class)
@Slf4j
public class SubmitServiceTest {

    @InjectMocks
    private SubmitServiceImpl submitService;

    @Mock
    private SubmitRepository submitRepository;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @ParameterizedTest
    @DisplayName("update submit status test")
    @MethodSource("provideUpdateStatus")
    public void testStatus(List<CompileResultDTO> results, boolean isResult, SubmitStatus status) {
        when(submitRepository.findById(1L))
                .thenReturn(Optional.of(new SubmitList()));
        UpdateSubmitResponse response = submitService.saveCompileResult(
                1L,
                results
        );
        assertThat(response.isResult()).isEqualTo(isResult);
        assertThat(response.getStatus()).isEqualTo(status);
    }

    private static Stream<Arguments> provideUpdateStatus() {
        return Stream.of(
                Arguments.of(
                        List.of(
                                new CompileResultDTO(
                                        CompileStatus.COMPILE_ERROR,
                                        null,
                                        null,
                                        "",
                                        new Exception()
                                )
                        ),
                        true,
                        SubmitStatus.COMPILE_ERROR
                ),
                Arguments.of(
                        List.of(
                                new CompileResultDTO(
                                        CompileStatus.WRONG,
                                        null,
                                        null,
                                        "",
                                        new Exception()
                                ),
                                new CompileResultDTO(
                                        CompileStatus.WRONG,
                                        null,
                                        null,
                                        "",
                                        new Exception()
                                ),
                                new CompileResultDTO(
                                        CompileStatus.CORRECT,
                                        null,
                                        null,
                                        "",
                                        new Exception()
                                ),
                                new CompileResultDTO(
                                        CompileStatus.CORRECT,
                                        null,
                                        null,
                                        "",
                                        new Exception()
                                )
                        ),
                        true,
                        SubmitStatus.WRONG
                ),
                Arguments.of(
                        List.of(
                                new CompileResultDTO(
                                        CompileStatus.WRONG,
                                        null,
                                        null,
                                        "",
                                        new Exception()
                                ),
                                new CompileResultDTO(
                                        CompileStatus.WRONG,
                                        null,
                                        null,
                                        "",
                                        new Exception()
                                ),
                                new CompileResultDTO(
                                        CompileStatus.RUNTIME_ERROR,
                                        null,
                                        null,
                                        "",
                                        new Exception()
                                ),
                                new CompileResultDTO(
                                        CompileStatus.CORRECT,
                                        null,
                                        null,
                                        "",
                                        new Exception()
                                )
                        ),
                        true,
                        SubmitStatus.RUNTIME_ERROR
                ),
                Arguments.of(
                        List.of(
                                new CompileResultDTO(
                                        CompileStatus.CORRECT,
                                        null,
                                        null,
                                        "",
                                        new Exception()
                                ),
                                new CompileResultDTO(
                                        CompileStatus.CORRECT,
                                        null,
                                        null,
                                        "",
                                        new Exception()
                                ),
                                new CompileResultDTO(
                                        CompileStatus.CORRECT,
                                        null,
                                        null,
                                        "",
                                        new Exception()
                                ),
                                new CompileResultDTO(
                                        CompileStatus.CORRECT,
                                        null,
                                        null,
                                        "",
                                        new Exception()
                                )
                        ),
                        true,
                        SubmitStatus.CORRECT
                )
        );
    }
}
