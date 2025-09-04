package com.sheoanna.teach_sphere.session_request.dtos;

import java.time.LocalDateTime;

public record SessionRequestResponse(Long id,
                                     String studentName,
                                     String subjectName,
                                     String message,
                                     String requestStatus,
                                     LocalDateTime createdAt) {
}
