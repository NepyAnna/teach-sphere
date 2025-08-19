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
    private  final UserService userService;

    public Page<ProfileResponse> findAll(Pageable pageable) {
        return profileRepository.findAll(pageable)
                .map(profileMapper::toResponse);
    }

    public ProfileResponse findById(Long id) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new ProfileNotFoundException(id));

        if (!userService.hasAccessToProfile(profile)) {
            throw new AccessDeniedException("You are not allowed to access this profile.");
        }
        return profileMapper.toResponse(profile);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ProfileResponse store(ProfileRequest newProfileData) {
        User user = userService.getAuthenticatedUser();

        if (profileRepository.findByUserId(user.getId()).isPresent()) {
            throw new ProfileAlreadyExistsException(
                    "Profile with user_id: " + user.getId() + " already exists!");
        }
        Profile profile = profileMapper.toEntity(newProfileData);
        profile.setUser(user);

        handlePhotoUpload(newProfileData, profile);
        //Profile savedProfile = profileRepository.save(profile);
        return profileMapper.toResponse(profileRepository.save(profile));
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
