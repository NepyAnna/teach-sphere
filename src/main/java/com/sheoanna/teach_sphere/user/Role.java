package com.sheoanna.teach_sphere.user;

import java.util.Set;

public enum Role {
    STUDENT(true),
    MENTOR(true),
    ADMIN(false);

    private final boolean allowedForSelfRegistration;

    Role(boolean allowedForSelfRegistration) {
        this.allowedForSelfRegistration = allowedForSelfRegistration;
    }

    public boolean isAllowedForSelfRegistration() {
        return allowedForSelfRegistration;
    }

    public static boolean allAllowed(Set<Role> roles) {
        return roles.stream().allMatch(Role::isAllowedForSelfRegistration);
    }
}
