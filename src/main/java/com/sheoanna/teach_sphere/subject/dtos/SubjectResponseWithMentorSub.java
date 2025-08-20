package com.sheoanna.teach_sphere.subject.dtos;

import com.sheoanna.teach_sphere.mentor_subject.MentorSubject;

import java.util.List;

public record SubjectResponseWithMentorSub(Long id,
                                           String subjectName,
                                           List<MentorSubject> mentorSubjects) {
}
