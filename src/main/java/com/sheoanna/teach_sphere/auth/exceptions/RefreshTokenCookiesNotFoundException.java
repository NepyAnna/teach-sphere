package com.sheoanna.teach_sphere.auth.exceptions;

import com.sheoanna.teach_sphere.global.AppException;

public class RefreshTokenCookiesNotFoundException extends AppException {
    public RefreshTokenCookiesNotFoundException() {
        super("Refresh token cookie missing");
    }
}