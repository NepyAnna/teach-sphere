package com.sheoanna.teach_sphere.user;

import com.sheoanna.teach_sphere.profile.Profile;
import com.sheoanna.teach_sphere.user.dtos.UserMapper;
import com.sheoanna.teach_sphere.user.dtos.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public Page<UserResponse> findAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toResponse);
    }

    public UserResponse findById(Long id) {
        User existedUser = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return userMapper.toResponse(existedUser);
    }

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public boolean isAdmin(User user) {
        return user.getRoles().stream()
                .map(Role::name)
                .anyMatch(roleName ->
                        roleName.equalsIgnoreCase("ADMIN") || roleName.equalsIgnoreCase("ROLE_ADMIN"));
    }

    public boolean hasAccessToProfile(Profile profile) {
        User user = getAuthenticatedUser();
        return isAdmin(user) || profile.getUser().getId().equals(user.getId());
    }
}
