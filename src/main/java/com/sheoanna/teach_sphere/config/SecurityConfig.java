package com.sheoanna.teach_sphere.config;

import com.sheoanna.teach_sphere.auth.filters.JwtAuthenticationFilter;
import com.sheoanna.teach_sphere.auth.filters.TokenBlacklistFilter;
import com.sheoanna.teach_sphere.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final TokenBlacklistFilter tokenBlacklistFilter;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable).sessionManagement(
                        session ->
                                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers("/api/auth/login", "/api/auth/register", "/api/auth/refresh")
                                .permitAll()
                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/health","/actuator/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/users", "/api/profiles")
                                .hasRole("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/categories", "/api/categories/**", "api/subjects", "api/subjects/**")
                                .hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/api/categories", "/api/categories/**", "api/subjects", "api/subjects/**")
                                .hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/api/categories", "/api/categories/**", "api/subjects", "api/subjects/**")
                                .hasRole("ADMIN")
                                .anyRequest()
                                .authenticated())
                .addFilterBefore(tokenBlacklistFilter, LogoutFilter.class)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(authenticationProvider());
        return http.build();
    }
}