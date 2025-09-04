package com.sheoanna.teach_sphere.subject.exceptions;

import com.sheoanna.teach_sphere.global.AppException;

public class SubjectByNameAlreadyExistsException extends AppException {
    public SubjectByNameAlreadyExistsException(String name) {
        super("Subject with name " + name + " already exists");
    }
}
