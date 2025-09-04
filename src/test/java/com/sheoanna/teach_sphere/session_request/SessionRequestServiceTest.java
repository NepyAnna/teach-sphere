package com.sheoanna.teach_sphere.session_request;

import com.sheoanna.teach_sphere.mentor_subject.MentorSubject;
import com.sheoanna.teach_sphere.mentor_subject.MentorSubjectService;
import com.sheoanna.teach_sphere.session_request.dtos.SessionRequestMapper;
import com.sheoanna.teach_sphere.session_request.dtos.SessionRequestRequest;
import com.sheoanna.teach_sphere.session_request.dtos.SessionRequestResponse;
import com.sheoanna.teach_sphere.user.Role;
import com.sheoanna.teach_sphere.user.User;
import com.sheoanna.teach_sphere.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionRequestServiceTest {
    @Mock
    private SessionRequestRepository sessionRepository;

    @Mock
    private SessionRequestMapper sessionMapper;

    @Mock
    private UserService userService;

    @Mock
    private MentorSubjectService mentorSubjectService;

    @InjectMocks
    private SessionRequestService service;

    private User user;
    private Pageable pageable;

    @BeforeEach
    void setUp(){
        user = new User();
        user.setId(1L);
        user.setRoles(Set.of(Role.STUDENT));

        pageable = PageRequest.of(0, 2);
    }

    @Test
    void findRequestsForStudent_Success() {
        SessionRequest request = new SessionRequest();
        request.setId(100L);

        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(sessionRepository.findByStudentId(user.getId(), pageable))
                .thenReturn(new PageImpl<>(List.of(request)));
        when(sessionMapper.toResponse(request)).thenReturn(new SessionRequestResponse(100L, null, null, null,null,null));

        Page<SessionRequestResponse> result = service.findRequestsForStudent(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(100L, result.getContent().get(0).id());
    }

    @Test
    void findRequestsForStudent_NotStudent() {
        user.setRoles(Set.of(Role.MENTOR));

        when(userService.getAuthenticatedUser()).thenReturn(user);

        assertThrows(RuntimeException.class, () -> service.findRequestsForStudent(PageRequest.of(0, 1)));
    }

    @Test
    void findRequestsForMentor_ShouldReturnMappedPage() {
        user.setRoles(Set.of(Role.MENTOR));

        SessionRequest request = new SessionRequest();
        request.setId(200L);
        request.setMentorSubject(new MentorSubject());
        request.getMentorSubject().setMentor(user);

        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(sessionRepository.findByMentorSubjectMentorId(user.getId(), pageable))
                .thenReturn(new PageImpl<>(List.of(request)));
        when(sessionMapper.toResponse(request)).thenReturn(new SessionRequestResponse(200L, null, null, null, null, null));

        Page<SessionRequestResponse> result = service.findRequestsForMentor(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(200L, result.getContent().get(0).id());
    }

    @Test
    void createSessionRequest_ShouldSaveAndReturnResponse() {
        MentorSubject mentorSubject = new MentorSubject();
        mentorSubject.setId(10L);

        SessionRequestRequest requestDto = new SessionRequestRequest(10L, null);
        SessionRequest entity = new SessionRequest();
        entity.setId(300L);

        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(mentorSubjectService.findByIdObj(10L)).thenReturn(mentorSubject);
        when(sessionMapper.toEntity(requestDto)).thenReturn(entity);
        when(sessionMapper.toResponse(entity)).thenReturn(new SessionRequestResponse(300L, null, null, null,null, null));

        SessionRequestResponse response = service.createSessionRequest(requestDto);

        verify(sessionRepository).save(entity);
        assertEquals(300L, response.id());
    }

    @Test
    void updateStatus_mentorNotOwner() {
        user.setRoles(Set.of(Role.MENTOR));

        SessionRequest request = new SessionRequest();
        MentorSubject mentorSubject = new MentorSubject();
        User otherMentor = new User();
        otherMentor.setId(999L);
        mentorSubject.setMentor(otherMentor);
        request.setMentorSubject(mentorSubject);

        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(request));

        assertThrows(AccessDeniedException.class, () -> service.updateStatus(1L, RequestStatus.ACCEPTED));
    }

    @Test
    void updateStatus_success() {
        user.setRoles(Set.of(Role.MENTOR));

        SessionRequest request = new SessionRequest();
        MentorSubject mentorSubject = new MentorSubject();
        mentorSubject.setMentor(user);
        request.setMentorSubject(mentorSubject);

        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(request));
        when(sessionMapper.toResponse(request)).thenReturn(new SessionRequestResponse(1L, null, null, null, null, null));

        SessionRequestResponse response = service.updateStatus(1L, RequestStatus.ACCEPTED);

        assertEquals(1L, response.id());
        assertEquals(RequestStatus.ACCEPTED, request.getStatus());
    }
}