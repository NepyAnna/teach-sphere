package com.sheoanna.teach_sphere.profile;

import com.sheoanna.teach_sphere.cloudinary.CloudinaryService;
import com.sheoanna.teach_sphere.profile.dtos.ProfileMapper;
import com.sheoanna.teach_sphere.profile.dtos.ProfileResponse;
import com.sheoanna.teach_sphere.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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


}
