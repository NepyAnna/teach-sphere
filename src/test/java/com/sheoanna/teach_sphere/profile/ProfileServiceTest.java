package com.sheoanna.teach_sphere.profile;

import com.sheoanna.teach_sphere.cloudinary.CloudinaryService;
import com.sheoanna.teach_sphere.cloudinary.UploadResult;
import com.sheoanna.teach_sphere.profile.dtos.ProfileMapper;
import com.sheoanna.teach_sphere.profile.dtos.ProfileRequest;
import com.sheoanna.teach_sphere.profile.dtos.ProfileResponse;
import com.sheoanna.teach_sphere.user.User;
import com.sheoanna.teach_sphere.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.multipart.MultipartFile;
import static org.mockito.ArgumentMatchers.any;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {
    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private UserService userService;

    @Mock
    private ProfileMapper profileMapper;

    @Mock
    private CloudinaryService cloudinaryService;

    @InjectMocks
    private ProfileService profileService;

    @Mock
    private MultipartFile multipartFile;

    @Mock
    private UploadResult uploadResult;

    private User user;
    private Profile profile;
    private ProfileRequest request;
    private ProfileResponse response;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).username("testUser").build();
        profile = Profile.builder().id(1L).user(user).build();
        request = new ProfileRequest( multipartFile,"My bio", "Location");
        response = new ProfileResponse(1L, "avatar url", "My bio", "Location", "testUser");
    }

    @Test
    void findAll_ReturnsPageOfProfiles() {
        Page<Profile> profiles = new PageImpl<>(List.of(profile));
        when(profileRepository.findAll(any(Pageable.class))).thenReturn(profiles);
        when(profileMapper.toResponse(profile)).thenReturn(response);

        Page<ProfileResponse> result = profileService.findAllProfiles(PageRequest.of(0, 4));

        assertEquals(1, result.getTotalElements());
        verify(profileMapper).toResponse(profile);
    }

    @Test
    void findById_WithAccess_ReturnsProfile() {
        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
        when(userService.hasAccessToProfile(profile)).thenReturn(true);
        when(profileMapper.toResponse(profile)).thenReturn(response);

        ProfileResponse result = profileService.findProfileById(1L);

        assertEquals(response, result);
    }

    @Test
    void findById_WithoutAccess_ThrowsAccessDenied() {
        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
        when(userService.hasAccessToProfile(profile)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> profileService.findProfileById(1L));
    }

    @Test
    void store_CreatesProfileSuccessfully() {
        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(profileRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(profileMapper.toEntity(request)).thenReturn(profile);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(cloudinaryService.upload(multipartFile, "profiles")).thenReturn(
                new UploadResult("avatar_url", "avatar_id"));
        when(profileRepository.save(any(Profile.class))).thenReturn(profile);
        when(profileMapper.toResponse(profile)).thenReturn(response);

        ProfileResponse result = profileService.createProfile(request);

        assertEquals("My bio", result.bio());
        verify(profileRepository).save(any(Profile.class));
    }

    @Test
    void update_UpdatesProfileSuccessfully() {
        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(profileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));
        when(multipartFile.isEmpty()).thenReturn(false);
        when(cloudinaryService.upload(any(), any())).thenReturn(new UploadResult("url", "publicId"));
        when(profileMapper.toResponse(profile)).thenReturn(response);

        ProfileResponse updated = profileService.updateProfile(1L,request);

        assertEquals("My bio", updated.bio());
        verify(cloudinaryService).upload(multipartFile, "profiles");
    }

    @Test
    void deleteById_WithAccess_DeletesProfile() {
        user.setProfile(profile);
        profile.setUser(user);
        profile.setAvatarPublicId("someId");

        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
        when(userService.hasAccessToProfile(profile)).thenReturn(true);

        profileService.deleteById(1L);

        verify(cloudinaryService).delete("someId");
        verify(profileRepository).deleteById(1L);
    }

    @Test
    void deleteById_WithoutAccess_ThrowsAccessDenied() {
        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
        when(userService.hasAccessToProfile(profile)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> profileService.deleteById(1L));
    }
}