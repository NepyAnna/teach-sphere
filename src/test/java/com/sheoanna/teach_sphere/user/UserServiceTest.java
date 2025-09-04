package com.sheoanna.teach_sphere.user;

import com.sheoanna.teach_sphere.profile.Profile;
import com.sheoanna.teach_sphere.user.dtos.UserMapper;
import com.sheoanna.teach_sphere.user.dtos.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .roles(Set.of(Role.STUDENT))
                .build();

        userResponse = new UserResponse(1L, "testuser");
    }

    @Test
    void findAllUsers_success() {
        PageRequest pageable = PageRequest.of(0, 5);
        when(userRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(user)));
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        Page<UserResponse> result = userService.findAllUsers(pageable);

        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).username()).isEqualTo("testuser");
        verify(userRepository).findAll(pageable);
        verify(userMapper).toResponse(user);
    }

    @Test
    void findById_found() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.findById(1L);

        assertThat(result.username()).isEqualTo("testuser");
        verify(userRepository).findById(1L);
    }

    @Test
    void findById_notFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(99L))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void getAuthenticatedUser_success() {
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken("testuser", "password")
        );
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        User result = userService.getAuthenticatedUser();

        assertThat(result.getUsername()).isEqualTo("testuser");
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void getAuthenticatedUser_notFound() {
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken("ghost", "password")
        );
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getAuthenticatedUser())
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void isAdmin_trueForAdmin() {
        User admin = User.builder().roles(Set.of(Role.ADMIN)).build();

        assertThat(userService.isAdmin(admin)).isTrue();
    }

    @Test
    void isAdmin_falseForStudent() {
        User student = User.builder().roles(Set.of(Role.STUDENT)).build();

        assertThat(userService.isAdmin(student)).isFalse();
    }

    @Test
    void hasAccessToProfile_asAdmin() {
        User admin = User.builder().id(2L).roles(Set.of(Role.ADMIN)).build();
        Profile profile = Profile.builder().user(user).build();
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken("admin", "password")
        );
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));

        assertThat(userService.hasAccessToProfile(profile)).isTrue();
    }

    @Test
    void hasAccessToProfile_asOwner() {
        Profile profile = Profile.builder().user(user).build();

        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken("testuser", "password")
        );
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        assertThat(userService.hasAccessToProfile(profile)).isTrue();
    }

    @Test
    void hasAccessToProfile_denied() {
        User anotherUser = User.builder().id(3L).username("other").roles(Set.of(Role.STUDENT)).build();
        Profile profile = Profile.builder().user(anotherUser).build();

        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken("testuser", "password")
        );
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        assertThat(userService.hasAccessToProfile(profile)).isFalse();
    }
}