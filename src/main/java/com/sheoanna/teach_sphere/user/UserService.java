package com.sheoanna.teach_sphere.user;

import com.sheoanna.teach_sphere.profile.Profile;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

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
