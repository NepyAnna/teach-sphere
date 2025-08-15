package com.sheoanna.teach_sphere.profile.dtos;

public record ProfileResponse(Long id,
                              String avatarUrl,
                              String bio,
                              String location,
                              String username) {
}
