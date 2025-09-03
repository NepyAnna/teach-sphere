package com.sheoanna.teach_sphere.review;

import com.sheoanna.teach_sphere.review.dtos.MentorSubjectReviewRequest;
import com.sheoanna.teach_sphere.review.dtos.MentorSubjectReviewResponse;
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
@RequestMapping("/api/mentor_subject_reviews")
@RequiredArgsConstructor
@Tag(name = "Mentor Subject Review", description = "Operations related to Mentor Subject Review.")
public class MentorSubjectReviewController {
    private final MentorSubjectReviewService reviewService;

    @GetMapping("")
    @Operation(summary = "Get all Mentor Subject Reviews.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Mentor Subject Reviews returned successfully."),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            })
    public Page<MentorSubjectReviewResponse> findAllReviews(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "4") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return reviewService.findAllReviews(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Mentor Subject Review by ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Mentor Subject Review returned successfully."),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/MentorSubjectReviewNotFound"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            })
    public ResponseEntity<MentorSubjectReviewResponse> findReviewById(@PathVariable Long id) {
        return ResponseEntity.ok()
                .body(reviewService.findReviewById(id));
    }

    @PostMapping("")
    @Operation(summary = "Create Mentor Subject Review.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Mentor Subject Review created successfully."),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            })
    public ResponseEntity<MentorSubjectReviewResponse> createReview(@Valid @RequestBody MentorSubjectReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.createReview(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Mentor Subject Review by ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Mentor Subject Review updated successfully."),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/MentorSubjectReviewNotFound"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            })
    public ResponseEntity<MentorSubjectReviewResponse> updateReview(@PathVariable Long id,
                                                                    @Valid @RequestBody MentorSubjectReviewRequest request) {
        return ResponseEntity.ok()
                .body(reviewService.updateReview(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Mentor Subject Review category by ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Mentor Subject Review deleted successfully."),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/MentorSubjectReviewNotFound"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            })
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}