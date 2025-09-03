package com.sheoanna.teach_sphere.mentor_subject;

import com.sheoanna.teach_sphere.mentor_subject.dtos.MentorMapper;
import com.sheoanna.teach_sphere.mentor_subject.dtos.MentorResponse;
import com.sheoanna.teach_sphere.mentor_subject.dtos.MentorSearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorSearchService {
    private final MentorSubjectRepository mentorSubjectRepository;
    private final MentorMapper mentorMapper;

    public List<MentorResponse> searchMentors(MentorSearchRequest request) {
        Specification<MentorSubject> spec = MentorSpecification.hasCategory(request.categoryId())
                .and(MentorSpecification.hasSubject(request.subjectId()))
                .and(MentorSpecification.hasLocation(request.location()));

        return mentorSubjectRepository.findAll(spec).stream()
                .map(MentorSubject::getMentor)
                .distinct()
                .map(mentorMapper::toResponse)
                .toList();
    }
}
