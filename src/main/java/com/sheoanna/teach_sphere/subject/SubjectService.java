package com.sheoanna.teach_sphere.subject;

import com.sheoanna.teach_sphere.subject.dtos.SubjectMapper;
import com.sheoanna.teach_sphere.subject.dtos.SubjectResponse;
import com.sheoanna.teach_sphere.subject.dtos.SubjectResponseWithMentorSub;
import com.sheoanna.teach_sphere.subject.exceptions.SubjectNotFoundByIDException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository subjectRepository;
    private final SubjectMapper subjectMapper;

    public Page<SubjectResponse> findAllSubjects(Pageable pageable){
        return subjectRepository.findAll(pageable).map(subjectMapper::toResponse);
    }

    public SubjectResponseWithMentorSub findSubjectById(Long id){
        Subject existSubject = subjectRepository.findById(id)
                .orElseThrow(()-> new SubjectNotFoundByIDException(id));
        return subjectMapper.toResponseWithMentorSub(existSubject);
    }
}
