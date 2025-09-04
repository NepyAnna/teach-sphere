package com.sheoanna.teach_sphere.auth;

import com.sheoanna.teach_sphere.auth.dtos.AuthRequest;
import com.sheoanna.teach_sphere.auth.dtos.AuthResponse;
import com.sheoanna.teach_sphere.auth.dtos.RegisterRequest;
import com.sheoanna.teach_sphere.auth.dtos.RegisterResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Auth", description = "Operations related to authentication and authorization.")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Create new user(register).",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User was created successfully"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            })
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest regicterDto) {
        return ResponseEntity.status(HttpStatus.SC_CREATED).body(authService.register(regicterDto));
    }

    @PostMapping("/login")
    @Operation(summary = "User login.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users login successfully"),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            })
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest loginDto,
                                                     HttpServletResponse response) {
        AuthResponse userResponse = authService.authenticate(loginDto, response);
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + userResponse.token())
                .body(userResponse);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh tokens.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tokens refreshed Successfully"),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            })
    public ResponseEntity<Void> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = authService.refreshToken(request, response);
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + accessToken)
                .build();
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users logged Out Successfully"),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            })
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        return ResponseEntity.ok("Logged Out Successfully");
    }
}