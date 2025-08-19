package com.sheoanna.teach_sphere.profile.exceptions;

import com.sheoanna.teach_sphere.global.AppException;

public class ProfileAlreadyExistsException extends AppException {
    public ProfileAlreadyExistsException(Long id) {
        super("Profile with ID " + id + " already exists!");
    }
    public ProfileAlreadyExistsException(String message) {
        super(message);
    }
}
