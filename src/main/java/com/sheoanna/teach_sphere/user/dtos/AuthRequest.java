package com.sheoanna.teach_sphere.user.dtos;

import jakarta.validation.constraints.NotBlank;

public record AuthRequest(@NotBlank
                          String username,
                          @NotBlank
                          String password) {
}
