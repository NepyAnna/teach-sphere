package com.sheoanna.teach_sphere.auth;

import com.sheoanna.teach_sphere.auth.dtos.AuthRequest;
import com.sheoanna.teach_sphere.auth.dtos.AuthResponse;
import com.sheoanna.teach_sphere.auth.dtos.RegisterRequest;
import com.sheoanna.teach_sphere.auth.dtos.RegisterResponse;
import com.sheoanna.teach_sphere.auth.exceptions.RefreshTokenCookiesNotFoundException;
import com.sheoanna.teach_sphere.redis.RedisService;
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

    public RegisterResponse register(RegisterRequest registerDto) {
        if (!Role.allAllowed(registerDto.roles())) {
            throw new IllegalArgumentException("Invalid role for registration");
        }
        User user = User.builder()
                .username(registerDto.username())
                .email(registerDto.email())
                .password(passwordEncoder.encode(registerDto.password()))
                .roles(registerDto.roles())
                .build();

        userRepository.save(user);

        return new RegisterResponse(user.getId(), user.getUsername(), user.getRoles());
    }

    public AuthResponse authenticate(AuthRequest loginDto, HttpServletResponse response) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.username(), loginDto.password()));

        User user = userRepository.findByUsername(loginDto.username())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        String jwtToken = jwtService.generateJwtToken(user.getUsername());
        String refreshToken = jwtService.generateRefreshToken(user.getUsername());

        redisService.saveToken(refreshToken);

        Cookie refreshTokenCookie = getRefreshTokenCookie(refreshToken);
        response.addCookie(refreshTokenCookie);

        return new AuthResponse(user.getId(), user.getUsername(), jwtToken, user.getRoles());
    }

    public String refreshToken(HttpServletRequest request, HttpServletResponse response) {
        validateCookies(request);
        Cookie refreshTokenCookie = Arrays.stream(request.getCookies())
                .filter(cookie -> "refresh_token".equals(cookie.getName()))
                .findFirst()
                .orElseThrow(RefreshTokenCookiesNotFoundException::new);
        String currentRefreshToken = refreshTokenCookie.getValue();

        if (redisService.isBlacklisted(currentRefreshToken)) {
            throw new RuntimeException("Refresh token is blacklisted");
        }
        if (!jwtService.validateToken(currentRefreshToken)) {
            invalidateToken(currentRefreshToken);
            throw new RuntimeException("Refresh token is not valid");
        }
        String username = jwtService.extractSubject(currentRefreshToken);
        String newJwtToken = jwtService.generateJwtToken(username);
        String newRefreshToken = jwtService.generateRefreshToken(username);

        invalidateToken(currentRefreshToken);
        redisService.saveToken(newRefreshToken);

        Cookie newRefreshCookie = getRefreshTokenCookie(newRefreshToken);
        response.addCookie(newRefreshCookie);

        return newJwtToken;
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        validateCookies(request);
        Cookie refreshTokenCookie = Arrays.stream(request.getCookies())
                .filter(cookie -> "refresh_token".equals(cookie.getName()))
                .findFirst()
                .orElseThrow(RefreshTokenCookiesNotFoundException::new);
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid Authorization header");
        }
        String accessToken = authHeader.substring(7);

        log.info("Token value from request: {}", accessToken);

        invalidateToken(refreshTokenCookie.getValue());
        invalidateToken(accessToken);
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
        long expirationMillis = jwtService.extractExpiration(token).getTime();
        long remainingSeconds = (expirationMillis - System.currentTimeMillis()) / 1000;
        int cookieMaxAge = remainingSeconds > 0 ? (int) remainingSeconds : 0;

        Cookie refreshTokenCookie = new Cookie("refresh_token", token);
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

    private void validateCookies(HttpServletRequest request) {
        if (request.getCookies() == null) {
            throw new RuntimeException("No cookies present");
        }
    }
}