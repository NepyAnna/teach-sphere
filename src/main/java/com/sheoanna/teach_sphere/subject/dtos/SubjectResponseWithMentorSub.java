package com.sheoanna.teach_sphere.subject.dtos;

import com.sheoanna.teach_sphere.mentor_subject.dtos.MentorSubjectResponse;

import java.util.List;

public record SubjectResponseWithMentorSub(Long id,
                                           String subjectName,
                                           List<MentorSubjectResponse> mentorSubjects) {
}
