package com.sheoanna.teach_sphere.subject;

import com.sheoanna.teach_sphere.category.Category;
import com.sheoanna.teach_sphere.category.CategoryService;
import com.sheoanna.teach_sphere.subject.dtos.SubjectMapper;
import com.sheoanna.teach_sphere.subject.dtos.SubjectRequest;
import com.sheoanna.teach_sphere.subject.dtos.SubjectResponse;
import com.sheoanna.teach_sphere.subject.dtos.SubjectResponseWithMentorSub;
import com.sheoanna.teach_sphere.subject.exceptions.SubjectByNameAlreadyExistsException;
import com.sheoanna.teach_sphere.subject.exceptions.SubjectNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository subjectRepository;
    private final SubjectMapper subjectMapper;
    private final CategoryService categoryService;

    public Page<SubjectResponse> findAllSubjects(Pageable pageable){
        return subjectRepository.findAll(pageable).map(subjectMapper::toResponse);
    }

    public SubjectResponseWithMentorSub findSubjectById(Long id){
        Subject existSubject = subjectRepository.findById(id)
                .orElseThrow(()-> new SubjectNotFoundException(id));

        return subjectMapper.toResponseWithMentorSub(existSubject);
    }

    @Transactional
    public SubjectResponse createSubject(SubjectRequest request) {
        Category existCategory = categoryService.findExistCategory(request.categoryId());

        if(subjectRepository.findByName(request.name())) {
            throw new SubjectByNameAlreadyExistsException(request.name());
        }

        Subject savedSubject = subjectRepository.save(Subject.builder()
                .name(request.name())
                .category(existCategory).build());

        return subjectMapper.toResponse(savedSubject);
    }
}
