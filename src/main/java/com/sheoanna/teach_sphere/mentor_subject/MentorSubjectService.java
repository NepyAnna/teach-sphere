package com.sheoanna.teach_sphere.mentor_subject;

import com.sheoanna.teach_sphere.mentor_subject.dtos.MentorSubjectMapper;
import com.sheoanna.teach_sphere.mentor_subject.dtos.MentorSubjectRequest;
import com.sheoanna.teach_sphere.mentor_subject.dtos.MentorSubjectResponse;
import com.sheoanna.teach_sphere.mentor_subject.exceptions.MentorSubjectNotFoundException;
import com.sheoanna.teach_sphere.subject.Subject;
import com.sheoanna.teach_sphere.subject.SubjectService;
import com.sheoanna.teach_sphere.user.Role;
import com.sheoanna.teach_sphere.user.User;
import com.sheoanna.teach_sphere.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MentorSubjectService {
    private final MentorSubjectRepository mentorSubjectRepository;
    private final MentorSubjectMapper mentorSubjectMapper;
    private final UserService userService;
    public final SubjectService subjectService;

    public Page<MentorSubjectResponse> findALlMentorSubjects(Pageable pageable) {
        return mentorSubjectRepository.findAll(pageable)
                .map(mentorSubjectMapper::toResponse);

    }

    public MentorSubjectResponse findById(Long id) {
        MentorSubject existMentorSubject = mentorSubjectRepository.findById(id)
                .orElseThrow(() -> new MentorSubjectNotFoundException(id));
        return mentorSubjectMapper.toResponse(existMentorSubject);
    }

    @Transactional
    public MentorSubjectResponse createMentorSubject(MentorSubjectRequest request) {
        User existMentor = userService.getAuthenticatedUser();
        Subject existSubject = subjectService.findSubjectByIdObj(request.subjectId());

        if (existMentor.getRoles().stream().noneMatch(role -> role == Role.MENTOR)) {
            throw new RuntimeException("Only mentor can add mentor subject.");
        }

        MentorSubject newMentorSubject = new MentorSubject();
        newMentorSubject.setMentor(existMentor);
        newMentorSubject.setSubject(existSubject);
        mentorSubjectRepository.save(newMentorSubject);

        return mentorSubjectMapper.toResponse(newMentorSubject);
    }

    @Transactional
    public MentorSubjectResponse updateMentorSubject(Long mentorSubjectId, MentorSubjectRequest request) {
        User existMentor = userService.getAuthenticatedUser();
        MentorSubject mentorSubject = mentorSubjectRepository.findById(mentorSubjectId)
                .orElseThrow(() -> new MentorSubjectNotFoundException(mentorSubjectId));
        checkCanModify(existMentor, mentorSubject);
        Subject existSubject = subjectService.findSubjectByIdObj(request.subjectId());
        mentorSubject.setSubject(existSubject);
        return mentorSubjectMapper.toResponse(mentorSubject);
    }

    @Transactional
    public void deleteMentorSubject(Long id) {
        User existMentor = userService.getAuthenticatedUser();
        MentorSubject mentorSubject = mentorSubjectRepository.findById(id)
                .orElseThrow(() -> new MentorSubjectNotFoundException(id));
        checkCanModify(existMentor, mentorSubject);
        mentorSubjectRepository.delete(mentorSubject);
    }

    public void checkCanModify(User existMentor, MentorSubject mentorSubject) {
        if (!mentorSubject.getMentor().getId().equals(existMentor.getId())) {
            throw new AccessDeniedException("You are not allowed to modify or delete this mentor subject.");
        }
    }
}
