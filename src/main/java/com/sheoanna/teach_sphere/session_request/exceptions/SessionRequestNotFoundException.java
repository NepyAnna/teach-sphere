package com.sheoanna.teach_sphere.session_request.exceptions;

import com.sheoanna.teach_sphere.global.AppException;

public class SessionRequestNotFoundException extends AppException {
    public SessionRequestNotFoundException(Long id) {
        super("Session request with ID " + id + " not found.");
    }
}
