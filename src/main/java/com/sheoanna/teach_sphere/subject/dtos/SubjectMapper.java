package com.sheoanna.teach_sphere.subject.dtos;
import com.sheoanna.teach_sphere.subject.Subject;
import org.springframework.stereotype.Component;

@Component
public class SubjectMapper {
    public SubjectResponse toResponse(Subject subject) {
        return new SubjectResponse(subject.getId(),
                subject.getName(),
                subject.getCategory().getName());
    }

/*    public SubjectResponseWithMentorSub toResponseWithMentorSub(Subject subject) {
        return new SubjectResponseWithMentorSub(subject.getId(),
                subject.getName(),
                subject.getMentorSubjects().);
    }*/

    public Subject toEntity(SubjectRequest request) {
        return Subject.builder()
                .name(request.name())
                .build();
    }
}
