package com.sheoanna.teach_sphere.review;

import com.sheoanna.teach_sphere.review.dtos.MentorSubjectReviewRequest;
import com.sheoanna.teach_sphere.review.dtos.MentorSubjectReviewResponse;
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
public class MentorSubjectReviewController {
    private final MentorSubjectReviewService reviewService;

    @GetMapping("")
    public Page<MentorSubjectReviewResponse> findAllReviews(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "4") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return reviewService.findAllReviews(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MentorSubjectReviewResponse> findReviewById(@PathVariable Long id) {
        return ResponseEntity.ok()
                .body(reviewService.findReviewById(id));
    }

    @PostMapping("")
    public ResponseEntity<MentorSubjectReviewResponse> createReview(@Valid @RequestBody MentorSubjectReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.createReview(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MentorSubjectReviewResponse> updateReview(@PathVariable Long id,
                                                                    @Valid @RequestBody MentorSubjectReviewRequest request) {
        return ResponseEntity.ok()
                .body(reviewService.updateReview(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
