package com.sheoanna.teach_sphere.auth.dtos;

import com.sheoanna.teach_sphere.user.Role;
import jakarta.validation.constraints.*;
import java.util.Set;

public record RegisterRequest(
        @NotBlank(message = "Username cannot be blank") @Size(max = 30, message = "Username cannot be longer than 30 characters!")
        String username,
        @NotBlank(message = "Email cannot be blank") @Email(message = "Email should be valid!")
        String email,
        @NotBlank(message = "Password cannot be blank") @Size(min = 8, message = "Password should be at least 8 characters long!")
        String password,
        @NotEmpty(message = "Roles cannot be empty")
        Set<@NotNull Role> roles) {
}
