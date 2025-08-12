package com.sheoanna.teach_sphere.auth.dtos;

import com.sheoanna.teach_sphere.user.Role;
import java.util.Set;

public record AuthResponse(Long id,
                           String username,
                           String token,
                           Set<Role> roles) {
}
