package com.sheoanna.teach_sphere.review.dtos;

import jakarta.validation.constraints.*;

public record MentorSubjectReviewRequest(@NotNull(message = "Rating is required") @PositiveOrZero(message = "Rating must be positive or zero")
                                         @DecimalMin(value = "0.0", inclusive = true, message = "Rating must be at least 0.0")
                                         @DecimalMax(value = "5.0", inclusive = true, message = "Rating must be at most 5.0") double rating,
                                         @NotBlank(message = "Review body must not be blank")
                                         @Size(max = 300, message = "Review must be at most 300 characters") String body,
                                         @NotNull(message = "Mentor subject ID is required") Long mentorSubjectId) {
}
