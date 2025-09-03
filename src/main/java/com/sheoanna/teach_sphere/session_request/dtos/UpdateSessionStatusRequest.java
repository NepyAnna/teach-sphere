package com.sheoanna.teach_sphere.session_request.dtos;

import com.sheoanna.teach_sphere.session_request.RequestStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateSessionStatusRequest(@NotNull RequestStatus status) {
}
