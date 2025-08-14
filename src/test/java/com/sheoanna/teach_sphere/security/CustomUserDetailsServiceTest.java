package com.sheoanna.teach_sphere.security;

import com.sheoanna.teach_sphere.user.User;
import com.sheoanna.teach_sphere.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("john");
        testUser.setPassword("password");
    }

    @Test
    void loadUserByUsernameTest_shouldReturnUserDetails_whenUserExists() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("john");

        assertNotNull(userDetails);
        assertEquals("john", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());

        verify(userRepository).findByUsername("john");
    }

    @Test
    void loadUserByUsernameTest_shouldThrowException_whenUserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                customUserDetailsService.loadUserByUsername("unknown")
        );

        verify(userRepository).findByUsername("unknown");
    }
}