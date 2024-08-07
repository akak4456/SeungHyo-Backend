package com.adele.seunghyobackend.security;

import com.adele.seunghyobackend.TestConfig;
import com.adele.seunghyobackend.member.auth.service.impl.RefreshTokenService;
import com.adele.seunghyobackend.security.dto.JwtToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.time.LocalDateTime;

import static com.adele.seunghyobackend.TestConstant.UNIT_TEST_TAG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
@SpringJUnitConfig(TestConfig.class)
@Tag(UNIT_TEST_TAG)
public class JwtAuthenticationFilterTest {
    @SpyBean
    private JwtTokenProvider jwtTokenProvider;
    private JwtAuthenticationFilter filter;
    @Autowired
    private RedisServer redisServer;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @BeforeEach
    public void setUp() {
        filter = new JwtAuthenticationFilter(jwtTokenProvider);
        redisServer.start();
    }

    @AfterEach
    public void destroy() {
        redisServer.stop();
    }
    @Test
    @DisplayName("정상적인 상황에서 do filter 가 정상 작동하는지 확인한다.")
    public void testDoFilter() throws ServletException, IOException {
        assertThat(filter).isNotNull();

        MockHttpServletRequest mockReq = new MockHttpServletRequest();
        MockHttpServletResponse mockResp = new MockHttpServletResponse();
        FilterChain mockChain = Mockito.mock(FilterChain.class);

        mockReq.setRequestURI("/");
        Authentication atc = new TestingAuthenticationToken("user1", null, "ROLE_ADMIN");
        mockReq.addHeader("Authorization", "Bearer " + jwtTokenProvider.generateToken(atc).getAccessToken());

        filter.doFilter(mockReq, mockResp, mockChain);

        verify(mockChain, times(1)).doFilter(mockReq, mockResp);
        verify(jwtTokenProvider, times(1)).getAuthentication(anyString());
    }

    @Test
    @DisplayName("이상한 토큰을 보냈을 때 오류를 체크하는지 확인한다.")
    public void testDoFilterError() throws ServletException, IOException {
        assertThat(filter).isNotNull();

        MockHttpServletRequest mockReq = new MockHttpServletRequest();
        MockHttpServletResponse mockResp = new MockHttpServletResponse();
        FilterChain mockChain = Mockito.mock(FilterChain.class);

        mockReq.setRequestURI("/");
        mockReq.addHeader("Authorization", "Bearer asdf" );

        filter.doFilter(mockReq, mockResp, mockChain);

        verify(mockChain, times(1)).doFilter(mockReq, mockResp);
        verify(jwtTokenProvider, times(0)).getAuthentication(anyString());
    }

    @Test
    @DisplayName("access token 이 expire 되면 refresh token 을 이용하는지 확인한다.")
    public void testDoFilterAccessTokenExpire() throws ServletException, IOException {
        MockHttpServletRequest mockReq = new MockHttpServletRequest();
        MockHttpServletResponse mockResp = new MockHttpServletResponse();
        FilterChain mockChain = Mockito.mock(FilterChain.class);

        mockReq.setRequestURI("/");
        Authentication atc = new TestingAuthenticationToken("user1", null, "ROLE_ADMIN");
        JwtToken token = jwtTokenProvider.generateToken(atc, LocalDateTime.now().minusSeconds(61));
        mockReq.addHeader("Authorization", "Bearer " + token.getAccessToken());
        mockReq.addHeader("Refresh-Token", token.getRefreshToken());
        refreshTokenService.saveRefreshToken("user1", token.getRefreshToken());
        filter.doFilter(mockReq, mockResp, mockChain);

        verify(mockChain, times(1)).doFilter(mockReq, mockResp);
        verify(jwtTokenProvider, times(1)).getAuthentication(anyString());
    }

    @Test
    @DisplayName("만약 refresh token 마저 expired 되면?")
    public void testDoFilterRefreshTokenExpire() throws ServletException, IOException {
        MockHttpServletRequest mockReq = new MockHttpServletRequest();
        MockHttpServletResponse mockResp = new MockHttpServletResponse();
        FilterChain mockChain = Mockito.mock(FilterChain.class);

        mockReq.setRequestURI("/");
        Authentication atc = new TestingAuthenticationToken("user1", null, "ROLE_ADMIN");
        JwtToken token = jwtTokenProvider.generateToken(atc, LocalDateTime.now().minusSeconds(86401));
        mockReq.addHeader("Authorization", "Bearer " + token.getAccessToken());
        mockReq.addHeader("Refresh-Token", token.getRefreshToken());
        refreshTokenService.saveRefreshToken("user1", token.getRefreshToken());
        filter.doFilter(mockReq, mockResp, mockChain);

        verify(mockChain, times(1)).doFilter(mockReq, mockResp);
        verify(jwtTokenProvider, times(0)).getAuthentication(anyString());
    }
}
