package com.sheoanna.teach_sphere.category.dtos;

import com.sheoanna.teach_sphere.category.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName());
    }

    public Category toEntity(CategoryRequest request) {
        return Category.builder().name(request.name()).build();
    }
}
