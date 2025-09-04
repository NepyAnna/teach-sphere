package com.sheoanna.teach_sphere.auth.filters;

import com.sheoanna.teach_sphere.auth.JwtService;
import com.sheoanna.teach_sphere.security.CustomUserDetailsService;
import com.sheoanna.teach_sphere.security.SecurityUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import java.io.IOException;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {
    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldPassWhenNoTokenOrAlreadyAuthenticated() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService);
    }

    @Test
    void shouldAuthenticateWhenValidAccessToken() throws ServletException, IOException {
        String token = "Bearer validtoken";
        String username = "testUser";

        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtService.extractSubject("validtoken")).thenReturn(username);
        when(jwtService.validateToken("validtoken")).thenReturn(true);
        when(jwtService.extractTokenType("validtoken")).thenReturn("access_token");

        SecurityUser mockUser = mock(SecurityUser.class);
        when(mockUser.getAuthorities()).thenReturn(null);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(mockUser);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        var auth = SecurityContextHolder.getContext().getAuthentication();
        verify(userDetailsService).loadUserByUsername(username);
        verify(filterChain).doFilter(request, response);
        assert auth instanceof UsernamePasswordAuthenticationToken;
    }

    @Test
    void shouldNotAuthenticateWhenInvalidToken() throws ServletException, IOException {
        String token = "Bearer invalidtoken";

        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtService.extractSubject("invalidtoken")).thenReturn("testUser");
        when(jwtService.validateToken("invalidtoken")).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assert SecurityContextHolder.getContext().getAuthentication() == null;
    }
}