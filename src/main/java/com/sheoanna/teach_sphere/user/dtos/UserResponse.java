package com.sheoanna.teach_sphere.user.dtos;

public record UserResponse(Long id,
                           String username,
                           String token,
                           String role) {
}
