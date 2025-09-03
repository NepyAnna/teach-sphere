package com.sheoanna.teach_sphere.profile;

import com.sheoanna.teach_sphere.profile.dtos.ProfileRequest;
import com.sheoanna.teach_sphere.profile.dtos.ProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Profile", description = "Operations related to profile.")
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping("")
    @Operation(summary = "Get all profiles.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Profiles returned successfully."),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            })
    public Page<ProfileResponse> showAllProfiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return profileService.findAllProfiles(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get profile by ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Category returned successfully."),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/ProfileNotFound"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            })
    public ResponseEntity<ProfileResponse> showProfileById(@PathVariable Long id) {
        return ResponseEntity.ok(profileService.findProfileById(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create profile.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Profile created successfully."),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            })
    public ResponseEntity<ProfileResponse> createProfile(@ModelAttribute ProfileRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(profileService.createProfile(request));
    }

    @PutMapping(value = "/{id}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update profile by ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Profile updated successfully."),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/ProfileNotFound"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            })
    public ResponseEntity<ProfileResponse> updateProfile(@PathVariable Long id, @ModelAttribute ProfileRequest request) {
        return ResponseEntity.ok(profileService.updateProfile(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete profile by ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Profile deleted successfully."),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/ProfileNotFound"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            })
    public ResponseEntity<Void> deleteProfileById(@PathVariable Long id) {
        profileService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
