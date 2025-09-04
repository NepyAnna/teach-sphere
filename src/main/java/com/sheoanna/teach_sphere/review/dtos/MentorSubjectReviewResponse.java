package com.sheoanna.teach_sphere.review.dtos;

import java.time.LocalDateTime;

public record MentorSubjectReviewResponse(Long id,
                                          double rating,
                                          String body,
                                          LocalDateTime createdAt,
                                          String username) {
}