package com.sheoanna.teach_sphere.subject.exceptions;

import com.sheoanna.teach_sphere.global.AppException;

public class SubjectNotFoundException extends AppException {
    public SubjectNotFoundException(String message) {
        super(message);
    }

    public SubjectNotFoundException(Long id) {
        super("Subject with ID " + id + " not found");
    }
}
