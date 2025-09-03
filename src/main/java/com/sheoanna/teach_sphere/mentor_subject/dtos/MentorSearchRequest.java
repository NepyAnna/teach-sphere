package com.sheoanna.teach_sphere.mentor_subject.dtos;

public record MentorSearchRequest(Long categoryId,
                                  Long subjectId,
                                  String location) {
}
