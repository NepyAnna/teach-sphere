package com.sheoanna.teach_sphere.auth;

import com.sheoanna.teach_sphere.auth.dtos.AuthRequest;
import com.sheoanna.teach_sphere.auth.dtos.AuthResponse;
import com.sheoanna.teach_sphere.auth.dtos.RegisterRequest;
import com.sheoanna.teach_sphere.auth.dtos.RegisterResponse;
import com.sheoanna.teach_sphere.email.EmailService;
import com.sheoanna.teach_sphere.redis.RedisService;
import com.sheoanna.teach_sphere.user.Role;
import com.sheoanna.teach_sphere.user.User;
import com.sheoanna.teach_sphere.user.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    JwtService jwtService;
    @Mock
    RedisService redisService;
    @Mock
    AuthenticationManager authenticationManager;
    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    private EmailService emailService;

    @InjectMocks
    AuthService authService;

    User testUser;
    Date futureDate;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("user1")
                .email("email@test.com")
                .password("encodedPass")
                .roles(Set.of(Role.STUDENT))
                .build();

        futureDate = new Date(System.currentTimeMillis() + 3600_000);

        lenient().when(jwtService.extractExpiration(anyString())).thenReturn(futureDate);
    }

    @Nested
    class RegisterTests {
        @Test
        void shouldRegister_WhenRoleAllowed() {
            RegisterRequest request = new RegisterRequest("user1", "email@test.com", "password", Set.of(Role.STUDENT));

            when(passwordEncoder.encode("password")).thenReturn("encodedPass");
            when(userRepository.save(any(User.class))).thenAnswer(inv -> {
                User user = inv.getArgument(0);
                user.setId(1L);
                return user;
            });

            RegisterResponse response = authService.register(request);

            assertEquals("user1", response.username());
            assertEquals(1L, response.id());
            verify(userRepository).save(any(User.class));
            verify(emailService).sendRegistrationEmail("email@test.com", "user1");
        }

        @Test
        void shouldThrow_WhenRoleNotAllowed() {
            RegisterRequest request = new RegisterRequest("user1", "email@test.com", "password", Set.of(Role.ADMIN));

            assertThrows(IllegalArgumentException.class, () -> authService.register(request));
            verifyNoInteractions(userRepository);
        }
    }

    @Nested
    class AuthenticateTests {
        HttpServletResponse servletResponse;

        @BeforeEach
        void setUp() {
            servletResponse = mock(HttpServletResponse.class);
        }

        @Test
        void shouldAuthenticate_WhenCredentialsValid() {
            AuthRequest request = new AuthRequest("user1", "password");

            when(userRepository.findByUsername("user1")).thenReturn(Optional.of(testUser));
            when(jwtService.generateJwtToken("user1")).thenReturn("jwt-token");
            when(jwtService.generateRefreshToken("user1")).thenReturn("refresh-token");

            AuthResponse response = authService.authenticate(request, servletResponse);

            assertEquals("jwt-token", response.token());
            verify(servletResponse).addCookie(any(Cookie.class));
        }

        @Test
        void shouldThrow_WhenUserNotFound() {
            AuthRequest request = new AuthRequest("user1", "password");

            when(userRepository.findByUsername("user1")).thenReturn(Optional.empty());

            assertThrows(UsernameNotFoundException.class,
                    () -> authService.authenticate(request, servletResponse));
        }
    }

    @Nested
    class RefreshTokenTests {
        HttpServletRequest servletRequest;
        HttpServletResponse servletResponse;

        @BeforeEach
        void setUp() {
            servletRequest = mock(HttpServletRequest.class);
            servletResponse = mock(HttpServletResponse.class);
        }

        @Test
        void shouldGenerateNewTokens_WhenValid() {
            Cookie cookie = new Cookie("refresh_token", "old-refresh");
            when(servletRequest.getCookies()).thenReturn(new Cookie[]{cookie});
            when(redisService.isBlacklisted("old-refresh")).thenReturn(false);
            when(jwtService.validateToken("old-refresh")).thenReturn(true);
            when(jwtService.extractSubject("old-refresh")).thenReturn("user1");
            when(jwtService.generateJwtToken("user1")).thenReturn("new-jwt");
            when(jwtService.generateRefreshToken("user1")).thenReturn("new-refresh");

            String token = authService.refreshToken(servletRequest, servletResponse);

            assertEquals("new-jwt", token);
            verify(servletResponse).addCookie(any(Cookie.class));
        }

        @Test
        void shouldThrow_WhenBlacklisted() {
            Cookie cookie = new Cookie("refresh_token", "old-refresh");
            when(servletRequest.getCookies()).thenReturn(new Cookie[]{cookie});
            when(redisService.isBlacklisted("old-refresh")).thenReturn(true);

            assertThrows(RuntimeException.class,
                    () -> authService.refreshToken(servletRequest, servletResponse));
        }
    }

    @Nested
    class LogoutTests {
        HttpServletRequest servletRequest;
        HttpServletResponse servletResponse;

        @BeforeEach
        void setUp() {
            servletRequest = mock(HttpServletRequest.class);
            servletResponse = mock(HttpServletResponse.class);
        }

        @Test
        void shouldInvalidateTokens() {
            Cookie cookie = new Cookie("refresh_token", "refresh-token");
            when(servletRequest.getCookies()).thenReturn(new Cookie[]{cookie});
            when(servletRequest.getHeader("Authorization")).thenReturn("Bearer access-token");

            authService.logout(servletRequest, servletResponse);

            verify(redisService, atLeastOnce())
                    .setTokenWithTTL(contains("blacklisted:"), eq("blacklisted"), anyLong(), eq(TimeUnit.MILLISECONDS));
        }
    }
}