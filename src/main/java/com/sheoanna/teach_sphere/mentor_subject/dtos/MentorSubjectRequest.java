package com.sheoanna.teach_sphere.mentor_subject.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MentorSubjectRequest(@NotNull(message = "Subject ID is required.")
                                   @Min(value = 1, message = ("Subject ID must be greater than 0."))
                                   Long subjectId) {
}
