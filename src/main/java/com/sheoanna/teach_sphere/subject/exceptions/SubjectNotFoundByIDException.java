package com.sheoanna.teach_sphere.subject.exceptions;

import com.sheoanna.teach_sphere.global.AppException;

public class SubjectNotFoundByIDException extends AppException {
    public SubjectNotFoundByIDException(String message) {
        super(message);
    }

    public SubjectNotFoundByIDException(Long id) {
        super("Subject with ID " + id + " not found");
    }
}
