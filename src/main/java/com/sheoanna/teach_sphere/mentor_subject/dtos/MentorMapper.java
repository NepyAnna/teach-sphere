package com.sheoanna.teach_sphere.mentor_subject.dtos;

import com.sheoanna.teach_sphere.user.User;
import org.springframework.stereotype.Component;

@Component
public class MentorMapper {
    public MentorResponse toResponse(User mentor) {
        return new MentorResponse(
                mentor.getId(),
                mentor.getUsername(),
                mentor.getProfile() != null ? mentor.getProfile().getBio() : null,
                mentor.getProfile() != null ? mentor.getProfile().getLocation() : null,
                mentor.getProfile() != null ? mentor.getProfile().getAvatarUrl() : null,
                mentor.getMentorSubjects()
                        .stream()
                        .map(ms -> ms.getSubject().getName())
                        .toList()
        );
    }
}