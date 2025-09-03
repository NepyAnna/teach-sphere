package com.sheoanna.teach_sphere.user.dtos;

import com.sheoanna.teach_sphere.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getUsername());
    }
}
