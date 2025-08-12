package com.sheoanna.teach_sphere.auth;

import com.sheoanna.teach_sphere.auth.dtos.AuthRequest;
import com.sheoanna.teach_sphere.auth.dtos.AuthResponse;
import com.sheoanna.teach_sphere.auth.dtos.RegisterRequest;
import com.sheoanna.teach_sphere.auth.dtos.RegisterResponse;
import com.sheoanna.teach_sphere.redis.RedisService;
import com.sheoanna.teach_sphere.security.CustomUserDetailsService;
import com.sheoanna.teach_sphere.user.Role;
import com.sheoanna.teach_sphere.user.User;
import com.sheoanna.teach_sphere.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.Cookie;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final RedisService redisService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse authenticate(AuthRequest loginDto, HttpServletResponse response) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.username(), loginDto.password()));

        User user = userRepository.findByUsername(loginDto.username())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String jwtToken = jwtService.generateJwtToken(user.getUsername());
        String refreshToken = jwtService.generateRefreshToken(user.getUsername());
        Cookie refreshTokenCookie = getRefreshTokenCookie(refreshToken);
        response.addCookie(refreshTokenCookie);

        return new AuthResponse(user.getId(), user.getUsername(), jwtToken, user.getRoles());
    }

    public String refreshToken(HttpServletRequest request, HttpServletResponse response) {
        var refreshTokenCookie = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("refresh_token")).findFirst().orElseThrow();
        var currentRefreshToken = refreshTokenCookie.getValue();

        if (redisService.hasToken(currentRefreshToken))
            return null;

        invalidateToken(currentRefreshToken);

        if (currentRefreshToken != null) {
            if (jwtService.validateToken(currentRefreshToken)) {
                var username = jwtService.extractSubject(currentRefreshToken);
                String jwtToken = jwtService.generateJwtToken(username);
                String newRefreshToken = jwtService.generateRefreshToken(username);
                refreshTokenCookie = getRefreshTokenCookie(newRefreshToken);
                response.addCookie(refreshTokenCookie);
                return jwtToken;
            }
        }
        return null;
    }

    public RegisterResponse register(RegisterRequest registerDto) {
        if (!Role.allAllowed(registerDto.roles())) {
            throw new IllegalArgumentException("Invalid role for registration");
        }

        User user = User.builder()
                .username(registerDto.username())
                .password(passwordEncoder.encode(registerDto.password()))
                .roles(registerDto.roles())
                .build();

        userRepository.save(user);

        return new RegisterResponse(user.getId(), user.getUsername(), user.getRoles());
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        var refreshTokenCookie = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("refresh_token")).findFirst().orElseThrow();
        var token = request.getHeader("Authorization").substring(7);

        log.info("Token value from request: {}", token);

        invalidateToken(token);
        invalidateToken(refreshTokenCookie.getValue());
        invalidateRefreshTokenCookie(response, refreshTokenCookie);
    }

    private void invalidateToken(String token) {
        long expirationTimeInMilliseconds = jwtService.extractExpiration(token).getTime() - System.currentTimeMillis();

        log.info("Invalidating token with remaining : TTL: {} seconds", expirationTimeInMilliseconds);

        if (expirationTimeInMilliseconds > 0L) {
            log.info("New Access Token Generated Successfully, Invalidating Previous Refresh token");
            redisService.setTokenWithTTL(token, "blacklisted", expirationTimeInMilliseconds, TimeUnit.MILLISECONDS);
        }
    }

    private Cookie getRefreshTokenCookie(String token) {
        var cookieMaxAge = (int) (jwtService.extractExpiration(token).getTime() - System.currentTimeMillis()) / 1000;

        if (cookieMaxAge < 0)
            cookieMaxAge = 0;

        Cookie refreshTokenCookie = new Cookie("refresh_token", (String) token);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(cookieMaxAge);

        return refreshTokenCookie;
    }

    private void invalidateRefreshTokenCookie(HttpServletResponse response, Cookie refreshTokenCookie) {
        refreshTokenCookie.setMaxAge(0);
        refreshTokenCookie.setPath("/");
        response.addCookie(refreshTokenCookie);
    }
}