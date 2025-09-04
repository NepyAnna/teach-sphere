package com.sheoanna.teach_sphere.mentor_subject.dtos;

import com.sheoanna.teach_sphere.mentor_subject.MentorSubject;
import org.springframework.stereotype.Component;

@Component
public class MentorSubjectMapper {
    public MentorSubjectResponse toResponse(MentorSubject mentorSubject){
        return new MentorSubjectResponse(mentorSubject.getId(),
                mentorSubject.getMentor().getUsername(),
                mentorSubject.getSubject().getName(),
                mentorSubject.getRating(),
                mentorSubject.getReviewCount());
    }
}
