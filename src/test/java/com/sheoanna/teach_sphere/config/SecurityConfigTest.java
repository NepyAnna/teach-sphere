package com.sheoanna.teach_sphere.config;

import com.sheoanna.teach_sphere.auth.JwtService;
import com.sheoanna.teach_sphere.auth.filters.JwtAuthenticationFilter;
import com.sheoanna.teach_sphere.auth.filters.TokenBlacklistFilter;
import com.sheoanna.teach_sphere.redis.RedisService;
import com.sheoanna.teach_sphere.security.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = true)
class SecurityConfigTest {
   @Autowired
    private MockMvc mockMvc;

     @MockitoBean
    private JwtAuthenticationFilter jwtAuthFilter;

    @MockitoBean
    private TokenBlacklistFilter tokenBlacklistFilter;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;
    @MockitoBean
    private RedisService redisService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void shouldPermitAccessToAuthEndpoints() throws Exception {
        mockMvc.perform(get("/auth/login"))
                .andExpect(status().isOk());
    }
}