package com.adele.seunghyobackend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.io.IOException;

import static com.adele.seunghyobackend.TestConstant.UNIT_TEST_TAG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
@Tag(UNIT_TEST_TAG)
public class JwtAuthenticationFilterTest {
    private JwtTokenProvider jwtTokenProvider;
    private JwtAuthenticationFilter filter;
    @BeforeEach
    public void setUp() {
        jwtTokenProvider = new JwtTokenProvider(
                "405212aeecd7145fcb537f236f338c99b0418dba1d1f7e0109d5f9acc267c7be", // 환경 변수에 있는 값과 다르다는 점에 유의
                86400,
                86400
        );
        jwtTokenProvider = spy(jwtTokenProvider);
        filter = new JwtAuthenticationFilter(jwtTokenProvider);
    }
    @Test
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
}
