package com.sheoanna.teach_sphere.auth.filters;

import com.sheoanna.teach_sphere.redis.RedisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.io.IOException;
import java.io.PrintWriter;
import static org.mockito.Mockito.*;

class TokenBlacklistFilterTest {
    @Mock
    private RedisService redisService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;
    @Mock
    private PrintWriter writer;
    @InjectMocks
    private TokenBlacklistFilter blacklistFilter;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    void shouldBlockWhenTokenBlacklisted() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer abc123");
        when(redisService.isBlacklisted("blacklisted:abc123")).thenReturn(true);

        blacklistFilter.doFilterInternal(request, response, filterChain);

        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is blacklisted");
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void shouldPassWhenTokenNotBlacklisted() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer abc123");
        when(redisService.isBlacklisted("blacklisted:abc123")).thenReturn(false);

        blacklistFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(response, never()).sendError(anyInt(), anyString());
    }

    @Test
    void shouldPassWhenNoToken() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        blacklistFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(response, never()).sendError(anyInt(), anyString());
    }
}