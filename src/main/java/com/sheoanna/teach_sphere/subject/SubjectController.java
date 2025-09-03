package com.sheoanna.teach_sphere.subject;

import com.sheoanna.teach_sphere.subject.dtos.SubjectRequest;
import com.sheoanna.teach_sphere.subject.dtos.SubjectResponse;
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
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
@Tag(name = "Subject", description = "Operations related to subject.")
public class SubjectController {
    private final SubjectService subjectService;

    @GetMapping("")
    @Operation(summary = "Get all subjects.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Subjects returned successfully"),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            })
    public Page<SubjectResponse> findAllSubjects(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "4") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return subjectService.findAllSubjects(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Subject category by ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Subject returned successfully"),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/SubjectNotFound"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            })
    public ResponseEntity<SubjectResponse> findSubjectById(@PathVariable Long id) {
        return ResponseEntity.ok().body(subjectService.findSubjectById(id));
    }

    @PostMapping("")
    @Operation(summary = "Create subject.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Subject created successfully"),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            })
    public ResponseEntity<SubjectResponse> createSubject(@Valid @RequestBody SubjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(subjectService.createSubject(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update subject by ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Subject updated successfully"),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/SubjectNotFound"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            })
    public ResponseEntity<SubjectResponse> updateSubject(@PathVariable Long id,@Valid @RequestBody SubjectRequest request) {
        return ResponseEntity.ok()
                .body(subjectService.updateSubject(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete subject by ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Subject deleted successfully"),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/SubjectNotFound"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            })
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.noContent().build();
    }
}
