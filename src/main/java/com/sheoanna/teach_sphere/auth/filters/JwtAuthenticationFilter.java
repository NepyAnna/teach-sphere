package com.sheoanna.teach_sphere.auth.filters;

import com.sheoanna.teach_sphere.auth.JwtService;
import com.sheoanna.teach_sphere.security.CustomUserDetailsService;
import com.sheoanna.teach_sphere.security.SecurityUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        SecurityContext context = SecurityContextHolder.getContext();
        String jwtToken = request.getHeader("Authorization");
        if (jwtToken == null || context.getAuthentication() != null) {
            filterChain.doFilter(request, response);
            logger.info("Access Token is not present in request.");
            return;
        }
        jwtToken = jwtToken.substring(7);
        String username = jwtService.extractSubject(jwtToken);
        if (username != null && jwtService.validateToken(jwtToken) && jwtService.extractTokenType(jwtToken).equals("access_token")) {
            logger.info("Access Token is present in request.");
            SecurityUser securityUser = (SecurityUser) userDetailsService.loadUserByUsername(username);
            var authToken = new UsernamePasswordAuthenticationToken(
                    securityUser,
                    null,
                    securityUser.getAuthorities()
            );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            context.setAuthentication(authToken);
        }
        filterChain.doFilter(request, response);
    }
}
