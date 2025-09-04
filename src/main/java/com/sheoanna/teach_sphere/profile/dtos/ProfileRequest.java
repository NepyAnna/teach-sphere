package com.sheoanna.teach_sphere.profile.dtos;

import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

public record ProfileRequest(MultipartFile avatar,
                             @NotBlank(message="BIO is required")
                             String bio,
                             String location) {
}
