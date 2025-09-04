package com.sheoanna.teach_sphere.mentor_subject.dtos;

public record MentorSubjectResponse(Long id,
                                    String mentorName,
                                    String subjectName,
                                    double rating,
                                    int count
                                    ) {
}
