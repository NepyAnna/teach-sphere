package com.sheoanna.teach_sphere.session_request.dtos;

import jakarta.validation.constraints.NotNull;

public record SessionRequestRequest(@NotNull(message = "Mentor subject ID is required") Long mentorSubjectId,
                                    String message) {
}
