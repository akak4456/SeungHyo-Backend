package com.adele.seunghyobackend.security;

import com.adele.seunghyobackend.TestConfig;
import com.adele.seunghyobackend.db.domain.Member;
import com.adele.seunghyobackend.db.repository.MemberRepository;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import redis.embedded.RedisServer;

import java.util.List;
import java.util.Optional;

import static com.adele.seunghyobackend.TestConstant.UNIT_TEST_TAG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(TestConfig.class)
@Tag(UNIT_TEST_TAG)
public class CustomUserDetailsServiceTest {
    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsServiceTest.class);
    @MockBean
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private RedisServer redisServer;

    @BeforeEach
    public void setUp() {
        redisServer.start();
        customUserDetailsService = new CustomUserDetailsService(memberRepository, passwordEncoder);
    }

    @AfterEach
    public void destroy() {
        redisServer.stop();
    }
    @Test
    @DisplayName("loadUserByUsername 이 정상 호출되는지 확인해본다.")
    public void loadUserByUsername() {
        when(memberRepository.findById(anyString())).thenReturn(Optional.of(new Member(
                "user1",
                "pass1",
                "status1",
                "Y",
                "email1",
                List.of("ADMIN")
        )));
        UserDetails details = customUserDetailsService.loadUserByUsername("user1");
        assertThat(details.getUsername()).isEqualTo("user1");
    }

    @Test
    @DisplayName("해당하는 유저가 없을 때 loadUserByUsername 이 오류를 일으키는지 확인해본다.")
    public void loadUserByUsernameFail() {
        assertThatCode(() -> customUserDetailsService.loadUserByUsername("user1"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("해당하는 회원을 찾을 수 없습니다.");
    }
}
