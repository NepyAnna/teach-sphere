package com.sheoanna.teach_sphere.review.dtos;

import com.sheoanna.teach_sphere.review.MentorSubjectReview;
import org.springframework.stereotype.Component;

@Component
public class MentorSubjectReviewMapper {
    public MentorSubjectReview toEntity(MentorSubjectReviewRequest request) {
        return MentorSubjectReview.builder()
                .rating(request.rating())
                .body(request.body())
                .build();
    }

    public MentorSubjectReviewResponse toResponse(MentorSubjectReview review) {
        return new MentorSubjectReviewResponse(review.getId(),
                review.getRating(),
                review.getBody(),
                review.getCreatedAt(),
                review.getReviewer().getUsername());
    }
}
