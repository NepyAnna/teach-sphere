package com.sheoanna.teach_sphere.profile.exceptions;

import com.sheoanna.teach_sphere.global.AppException;

public class ProfileNotFoundException extends AppException {
    public ProfileNotFoundException(Long id) {
        super("Profile with ID " + id +  " not found.");
    }
    public ProfileNotFoundException(String message) {
        super(message);
    }
}
