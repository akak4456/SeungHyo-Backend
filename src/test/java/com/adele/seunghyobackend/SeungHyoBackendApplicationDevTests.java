package com.adele.seunghyobackend;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static com.adele.seunghyobackend.TestConstant.INTEGRATED_TAG;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("dev")
@Tag(INTEGRATED_TAG)
class SeungHyoBackendApplicationDevTests {
    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${server.port}")
    private Long port;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${cross-origin-url}")
    private String crossOriginUrl;

    @Test
    @DisplayName("application-dev.yml 이 제대로 설정되었는지 확인한다.")
    void contextLoads() {
        assertThat(dbUrl).isNotBlank();
        assertThat(port).isNotNull();
        assertThat(username).isNotBlank();
        assertThat(password).isNotBlank();
        assertThat(jwtSecret).isNotBlank();
        assertThat(crossOriginUrl).isNotBlank();
    }

}
