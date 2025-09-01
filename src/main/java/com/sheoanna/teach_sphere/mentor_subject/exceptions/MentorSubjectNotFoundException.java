package com.sheoanna.teach_sphere.mentor_subject.exceptions;

import com.sheoanna.teach_sphere.global.AppException;

public class MentorSubjectNotFoundException extends AppException {
    public MentorSubjectNotFoundException(String message) {
        super(message);
    }
    public MentorSubjectNotFoundException(Long id) {
        super("Mentor subject with id " + id + " not found.");
    }
}
