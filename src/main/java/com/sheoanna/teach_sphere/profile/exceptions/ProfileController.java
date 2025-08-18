package com.sheoanna.teach_sphere.profile.exceptions;

import com.sheoanna.teach_sphere.profile.ProfileService;
import com.sheoanna.teach_sphere.profile.dtos.ProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {
    private  final ProfileService profileService;

    @GetMapping("")
    public Page<ProfileResponse> showAllProfiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return profileService.findAll(pageable);
    }
}
