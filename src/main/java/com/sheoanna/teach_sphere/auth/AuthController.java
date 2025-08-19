package com.sheoanna.teach_sphere.auth;

import com.sheoanna.teach_sphere.auth.dtos.AuthRequest;
import com.sheoanna.teach_sphere.auth.dtos.AuthResponse;
import com.sheoanna.teach_sphere.auth.dtos.RegisterRequest;
import com.sheoanna.teach_sphere.auth.dtos.RegisterResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest regicterDto) {
        return ResponseEntity.status(HttpStatus.SC_CREATED).body(authService.register(regicterDto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest loginDto,
                                                     HttpServletResponse response) {
        AuthResponse userResponse = authService.authenticate(loginDto, response);
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + userResponse.token())
                .body(userResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = authService.refreshToken(request, response);
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + accessToken)
                .build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        return ResponseEntity.ok("Logged Out Successfully");
    }
}