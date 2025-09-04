package com.sheoanna.teach_sphere.session_request.dtos;

import com.sheoanna.teach_sphere.session_request.RequestStatus;
import com.sheoanna.teach_sphere.session_request.SessionRequest;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class SessionRequestMapper {
    public SessionRequest toEntity(SessionRequestRequest request){
        return SessionRequest.builder()
                .message(request.message())
                .status(RequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public SessionRequestResponse toResponse(SessionRequest requestEntity) {
        return new SessionRequestResponse(requestEntity.getId(),
                requestEntity.getStudent().getUsername(),
                requestEntity.getMentorSubject().getSubject().getName(),
                requestEntity.getMessage(),
                requestEntity.getStatus().name(),
                requestEntity.getCreatedAt());
    }
}
