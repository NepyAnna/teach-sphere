package com.sheoanna.teach_sphere.profile.dtos;

import com.sheoanna.teach_sphere.profile.Profile;
import org.springframework.stereotype.Component;

@Component
public class ProfileMapper {
    public Profile toEntity(ProfileRequest request) {
        return Profile.builder()
                .bio(request.bio())
                .location(request.location())
                .build();
    }

    public ProfileResponse toResponse(Profile profile) {
        return  new ProfileResponse(profile.getId(),
                profile.getAvatarUrl(),
                profile.getBio(),
                profile.getLocation(),
                profile.getUser().getUsername());
    }
}
