package com.sheoanna.teach_sphere.subject.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubjectRequest(@NotBlank(message = "Subject name is required")
                             String name,
                             @NotNull(message = "Category ID is required")
                             Long categoryId) {
}
