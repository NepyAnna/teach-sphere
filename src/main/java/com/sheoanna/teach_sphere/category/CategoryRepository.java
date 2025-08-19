package com.sheoanna.teach_sphere.category;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByName(@NotBlank(message="Category name is required") String name);
}
