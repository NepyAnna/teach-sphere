package com.sheoanna.teach_sphere.profile;

import com.sheoanna.teach_sphere.cloudinary.CloudinaryService;
import com.sheoanna.teach_sphere.cloudinary.UploadResult;
import com.sheoanna.teach_sphere.profile.dtos.ProfileMapper;
import com.sheoanna.teach_sphere.profile.dtos.ProfileRequest;
import com.sheoanna.teach_sphere.profile.dtos.ProfileResponse;
import com.sheoanna.teach_sphere.profile.exceptions.ProfileAlreadyExistsException;
import com.sheoanna.teach_sphere.profile.exceptions.ProfileNotFoundException;
import com.sheoanna.teach_sphere.user.User;
import com.sheoanna.teach_sphere.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;
    private final CloudinaryService cloudinaryService;
    private final UserService userService;

    public Page<ProfileResponse> findAllProfiles(Pageable pageable) {
        return profileRepository.findAll(pageable)
                .map(profileMapper::toResponse);
    }

    public ProfileResponse findProfileById(Long id) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new ProfileNotFoundException(id));

        if (!userService.hasAccessToProfile(profile)) {
            throw new AccessDeniedException("You are not allowed to access this profile.");
        }
        return profileMapper.toResponse(profile);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ProfileResponse createProfile(ProfileRequest newProfileData) {
        User user = userService.getAuthenticatedUser();

        if (profileRepository.findByUserId(user.getId()).isPresent()) {
            throw new ProfileAlreadyExistsException(user.getId());
        }
        Profile profile = profileMapper.toEntity(newProfileData);
        profile.setUser(user);

        handlePhotoUpload(newProfileData, profile);
        return profileMapper.toResponse(profileRepository.save(profile));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ProfileResponse updateProfile(ProfileRequest newProfileData) {
        User user = userService.getAuthenticatedUser();
        Profile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ProfileNotFoundException("Profile for user ID " + user.getId() + " not found"));

        MultipartFile image = newProfileData.avatar();
        if (image != null && !image.isEmpty()) {
            if (profile.getAvatarPublicId() != null) {
                cloudinaryService.delete(profile.getAvatarPublicId());
            }
            UploadResult result = cloudinaryService.upload(image, "profiles");
            profile.setAvatarUrl(result.url());
            profile.setAvatarPublicId(result.publicId());
        }
        profileRepository.save(profile);
        return profileMapper.toResponse(profile);
    }

    @Transactional
    public void deleteById(Long id) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new ProfileNotFoundException(id));

        if (!userService.hasAccessToProfile(profile)) {
            throw new AccessDeniedException("You are not allowed to access this profile.");
        }
        User user = profile.getUser();

        if (user != null) {
            user.setProfile(null);
        }
        if (profile.getAvatarPublicId() != null) {
            cloudinaryService.delete(profile.getAvatarPublicId());
        }
        profileRepository.deleteById(id);
    }

    private void handlePhotoUpload(ProfileRequest newProfileData, Profile profile) {
        MultipartFile image = newProfileData.avatar();
        UploadResult result;

        if (image != null && !image.isEmpty()) {
            result = cloudinaryService.upload(image, "profiles");
        } else {
            result = cloudinaryService.uploadDefault("profiles");
        }
        profile.setAvatarUrl(result.url());
        profile.setAvatarPublicId(result.publicId());
    }
}
