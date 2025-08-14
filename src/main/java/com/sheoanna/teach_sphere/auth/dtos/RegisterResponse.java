package com.sheoanna.teach_sphere.auth.dtos;

import com.sheoanna.teach_sphere.user.Role;
import java.util.Set;

public record RegisterResponse(Long id,
                               String username,
                               Set<Role> roles) {
}
