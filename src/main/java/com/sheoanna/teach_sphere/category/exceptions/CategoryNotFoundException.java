package com.sheoanna.teach_sphere.category.exceptions;

import com.sheoanna.teach_sphere.global.AppException;

public class CategoryNotFoundException extends AppException {
    public CategoryNotFoundException(Long id) {
        super("Category with ID " + id + " not found");
    }
    public CategoryNotFoundException(String message) {
        super(message);
    }
}
