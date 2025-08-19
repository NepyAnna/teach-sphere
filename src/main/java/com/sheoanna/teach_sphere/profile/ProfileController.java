package com.sheoanna.teach_sphere.profile;

import com.sheoanna.teach_sphere.profile.dtos.ProfileRequest;
import com.sheoanna.teach_sphere.profile.dtos.ProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping("")
    public Page<ProfileResponse> showAllProfiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return profileService.findAllProfiles(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfileResponse> showProfileById(@PathVariable Long id) {
        return ResponseEntity.ok(profileService.findProfileById(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProfileResponse> createProfile(@ModelAttribute ProfileRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(profileService.createProfile(request));
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProfileResponse> updateProfile(@ModelAttribute ProfileRequest request) {
        return ResponseEntity.ok(profileService.updateProfile(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfileById(@PathVariable Long id) {
        profileService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
