package com.sheoanna.teach_sphere.profile;

import com.sheoanna.teach_sphere.cloudinary.CloudinaryService;
import com.sheoanna.teach_sphere.profile.dtos.ProfileMapper;
import com.sheoanna.teach_sphere.profile.dtos.ProfileResponse;
import com.sheoanna.teach_sphere.profile.exceptions.ProfileNotFoundException;
import com.sheoanna.teach_sphere.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

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
}
