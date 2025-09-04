package com.sheoanna.teach_sphere.mentor_subject.dtos;

import java.util.List;

public record MentorResponse(Long mentorId,
                             String username,
                             String bio,
                             String location,
                             String avatarUrl,
                             List<String> subjects) {
}