package com.sheoanna.teach_sphere.mentor_subject;

import com.sheoanna.teach_sphere.mentor_subject.dtos.MentorSubjectRequest;
import com.sheoanna.teach_sphere.mentor_subject.dtos.MentorSubjectResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mentor_subjects")
@RequiredArgsConstructor
@Tag(name = "Mentor Subject", description = "Operations related to mentor subjects.")
public class MentorSubjectController {
    private final MentorSubjectService mentorSubjectService;

    @GetMapping("")
    @Operation(summary = "Get all mentor subjects.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Mentor Subjects returned successfully."),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            })
    public Page<MentorSubjectResponse> findALlMentorSubjects(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "4") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return mentorSubjectService.findALlMentorSubjects(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Mentor Subject by ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Mentor Subject returned successfully."),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/MentorSubjectNotFound"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            })
    public ResponseEntity<MentorSubjectResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok()
                .body(mentorSubjectService.findById(id));
    }

    @PostMapping("")
    @Operation(summary = "Create Mentor Subject.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Mentor Subject created successfully."),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            })
    public ResponseEntity<MentorSubjectResponse> createMentorSubject(@Valid @RequestBody MentorSubjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mentorSubjectService.createMentorSubject(request));
    }

    @PutMapping("/{mentorSubjectId}")
    @Operation(summary = "Update Mentor Subject by ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Mentor Subject updated successfully."),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/MentorSubjectNotFound"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            })
    public ResponseEntity<MentorSubjectResponse> updateMentorSubject(@PathVariable Long mentorSubjectId, @Valid @RequestBody MentorSubjectRequest request) {
        return ResponseEntity.ok()
                .body(mentorSubjectService.updateMentorSubject(mentorSubjectId, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Mentor Subject by ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Mentor Subject deleted successfully."),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/MentorSubjectNotFound"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            })
    public ResponseEntity<Void> deleteMentorSubject(@PathVariable Long id) {
        mentorSubjectService.deleteMentorSubject(id);
        return ResponseEntity.noContent().build();
    }
}
