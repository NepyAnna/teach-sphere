package com.sheoanna.teach_sphere.session_request;

import com.sheoanna.teach_sphere.mentor_subject.MentorSubject;
import com.sheoanna.teach_sphere.mentor_subject.MentorSubjectService;
import com.sheoanna.teach_sphere.session_request.dtos.SessionRequestMapper;
import com.sheoanna.teach_sphere.session_request.dtos.SessionRequestRequest;
import com.sheoanna.teach_sphere.session_request.dtos.SessionRequestResponse;
import com.sheoanna.teach_sphere.session_request.exceptions.SessionRequestNotFoundException;
import com.sheoanna.teach_sphere.user.Role;
import com.sheoanna.teach_sphere.user.User;
import com.sheoanna.teach_sphere.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionRequestService {
    private final SessionRequestRepository sessionRepository;
    private final SessionRequestMapper sessionMapper;
    private final UserService userService;
    private final MentorSubjectService mentorSubjectService;

    public Page<SessionRequestResponse> findRequestsForStudent(Pageable pageable) {
        User student = userService.getAuthenticatedUser();
        checkIfStudent(student);
        return sessionRepository.findByStudentId(student.getId(),pageable)
                .map(sessionMapper::toResponse);
    }

    public Page<SessionRequestResponse> findRequestsForMentor(Pageable pageable) {
        User mentor = userService.getAuthenticatedUser();
        checkIfMentor(mentor);
        return sessionRepository.findByMentorSubjectMentorId(mentor.getId(), pageable)
                .map(sessionMapper::toResponse);
    }

    @Transactional
    public SessionRequestResponse createSessionRequest(SessionRequestRequest request) {
        User student = userService.getAuthenticatedUser();
        MentorSubject mentorSubject = mentorSubjectService.findByIdObj(request.mentorSubjectId());
        checkIfStudent(student);

        SessionRequest newSessionRequest = sessionMapper.toEntity(request);
        newSessionRequest.setMentorSubject(mentorSubject);
        newSessionRequest.setStudent(student);

        sessionRepository.save(newSessionRequest);

        return sessionMapper.toResponse(newSessionRequest);
    }

    @Transactional
    public SessionRequestResponse updateStatus(Long requestId, RequestStatus status) {
        User mentor = userService.getAuthenticatedUser();
        SessionRequest request = sessionRepository.findById(requestId)
                .orElseThrow(() -> new SessionRequestNotFoundException(requestId));

        if (!request.getMentorSubject().getMentor().getId().equals(mentor.getId())) {
            throw new AccessDeniedException("You are not allowed to modify session request.");
        }
        request.setStatus(status);

        return sessionMapper.toResponse(request);
    }

    public void checkIfStudent(User user){
        if (user.getRoles().stream().noneMatch(role -> role == Role.STUDENT)) {
            throw new RuntimeException("Only user-student allowed.");
        }
    }

    public void checkIfMentor(User user){
        if (user.getRoles().stream().noneMatch(role -> role == Role.MENTOR)) {
            throw new RuntimeException("Only user-mentor allowed.");
        }
    }
}
