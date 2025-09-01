package com.sheoanna.teach_sphere.review.exception;

import com.sheoanna.teach_sphere.global.AppException;

public class MentorSubjectReviewNotFoundException extends AppException {
    public MentorSubjectReviewNotFoundException(Long id) {
        super("Mentor subject review with id " + id + " not found.");
    }
}
