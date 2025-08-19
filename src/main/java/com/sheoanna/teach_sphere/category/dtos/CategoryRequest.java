package com.sheoanna.teach_sphere.category.dtos;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(@NotBlank(message="Category name is required") String name) {
}
