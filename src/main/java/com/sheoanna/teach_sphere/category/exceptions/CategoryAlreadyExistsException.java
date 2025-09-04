package com.sheoanna.teach_sphere.category.exceptions;

import com.sheoanna.teach_sphere.global.AppException;

public class CategoryAlreadyExistsException extends AppException {
    public CategoryAlreadyExistsException(String name) {
        super("Category with name " + name + " already exists");
    }
}
