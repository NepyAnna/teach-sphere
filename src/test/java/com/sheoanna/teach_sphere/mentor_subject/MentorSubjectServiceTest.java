package com.sheoanna.teach_sphere.mentor_subject;

import com.sheoanna.teach_sphere.mentor_subject.dtos.MentorSubjectMapper;
import com.sheoanna.teach_sphere.mentor_subject.dtos.MentorSubjectRequest;
import com.sheoanna.teach_sphere.mentor_subject.dtos.MentorSubjectResponse;
import com.sheoanna.teach_sphere.mentor_subject.exceptions.MentorSubjectNotFoundException;
import com.sheoanna.teach_sphere.review.MentorSubjectReview;
import com.sheoanna.teach_sphere.subject.Subject;
import com.sheoanna.teach_sphere.subject.SubjectService;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MentorSubjectServiceTest {
    @Mock
    private MentorSubjectRepository mentorSubjectRepository;
    @Mock
    private MentorSubjectMapper mentorSubjectMapper;
    @Mock
    private UserService userService;
    @Mock
    private SubjectService subjectService;

    @InjectMocks
    private MentorSubjectService mentorSubjectService;

    private User mentor;
    private User anotherMentor;
    private Subject subject;
    private MentorSubject mentorSubject;

    @BeforeEach
    void setUp() {
        mentor = User.builder().id(1L).roles(Set.of(Role.MENTOR)).build();
        anotherMentor = User.builder().id(2L).roles(Set.of(Role.MENTOR)).build();
        subject = Subject.builder().id(10L).name("Math").build();
        mentorSubject = MentorSubject.builder()
                .id(100L)
                .mentor(mentor)
                .subject(subject)
                .rating(0.0)
                .reviewCount(0)
                .build();
    }

    @Test
    void findAllMentorSubjects_success() {
        MentorSubjectResponse response = new MentorSubjectResponse(100L, "mentor", "Math", 4.0, 1);
        when(mentorSubjectRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(mentorSubject)));
        when(mentorSubjectMapper.toResponse(mentorSubject)).thenReturn(response);

        Page<MentorSubjectResponse> result = mentorSubjectService.findALlMentorSubjects(PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertThat(result.getContent().get(0).subjectName()).isEqualTo("Math");
        verify(mentorSubjectRepository).findAll(any(Pageable.class));
    }

    @Test
    void findById_success() {
        MentorSubjectResponse response = new MentorSubjectResponse(100L, "mentor", "Math", 4.0, 1);
        when(mentorSubjectRepository.findById(100L)).thenReturn(Optional.of(mentorSubject));
        when(mentorSubjectMapper.toResponse(mentorSubject)).thenReturn(response);

        MentorSubjectResponse result = mentorSubjectService.findById(100L);

        assertThat(result.subjectName()).isEqualTo("Math");
    }

    @Test
    void findById_notFound() {
        when(mentorSubjectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mentorSubjectService.findById(99L))
                .isInstanceOf(MentorSubjectNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void createMentorSubject_success() {
        MentorSubjectRequest request = new MentorSubjectRequest(10L);
        MentorSubjectResponse response = new MentorSubjectResponse(100L, "mentor", "Math", 4.0, 1);

        when(userService.getAuthenticatedUser()).thenReturn(mentor);
        when(subjectService.findSubjectByIdObj(10L)).thenReturn(subject);
        when(mentorSubjectMapper.toResponse(any(MentorSubject.class))).thenReturn(response);

        MentorSubjectResponse result = mentorSubjectService.createMentorSubject(request);

        assertThat(result.subjectName()).isEqualTo("Math");
        verify(mentorSubjectRepository).save(any(MentorSubject.class));
    }

    @Test
    void createMentorSubject_notMentor() {
        User student = User.builder().id(3L).roles(Set.of(Role.STUDENT)).build();
        MentorSubjectRequest request = new MentorSubjectRequest(10L);

        when(userService.getAuthenticatedUser()).thenReturn(student);

        assertThatThrownBy(() -> mentorSubjectService.createMentorSubject(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Only mentor can add mentor subject.");
    }

    @Test
    void updateMentorSubject_success() {
        MentorSubjectRequest request = new MentorSubjectRequest(10L);
        MentorSubjectResponse response = new MentorSubjectResponse(100L, "mentor", "Math", 4.0, 1);

        when(userService.getAuthenticatedUser()).thenReturn(mentor);
        when(mentorSubjectRepository.findById(100L)).thenReturn(Optional.of(mentorSubject));
        when(subjectService.findSubjectByIdObj(10L)).thenReturn(subject);
        when(mentorSubjectMapper.toResponse(mentorSubject)).thenReturn(response);

        MentorSubjectResponse result = mentorSubjectService.updateMentorSubject(100L, request);

        assertThat(result.subjectName()).isEqualTo("Math");
    }

    @Test
    void updateMentorSubject_notOwner() {
        MentorSubjectRequest request = new MentorSubjectRequest(10L);
        mentorSubject.setMentor(anotherMentor);

        when(userService.getAuthenticatedUser()).thenReturn(mentor);
        when(mentorSubjectRepository.findById(100L)).thenReturn(Optional.of(mentorSubject));

        assertThatThrownBy(() -> mentorSubjectService.updateMentorSubject(100L, request))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("You are not allowed to modify or delete this mentor subject.");
    }

    @Test
    void deleteMentorSubject_success() {
        when(userService.getAuthenticatedUser()).thenReturn(mentor);
        when(mentorSubjectRepository.findById(100L)).thenReturn(Optional.of(mentorSubject));

        mentorSubjectService.deleteMentorSubject(100L);

        verify(mentorSubjectRepository).delete(mentorSubject);
    }

    @Test
    void deleteMentorSubject_notOwner() {
        mentorSubject.setMentor(anotherMentor);
        when(userService.getAuthenticatedUser()).thenReturn(mentor);
        when(mentorSubjectRepository.findById(100L)).thenReturn(Optional.of(mentorSubject));

        assertThatThrownBy(() -> mentorSubjectService.deleteMentorSubject(100L))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("You are not allowed to modify or delete this mentor subject.");
    }

    @Test
    void updateRatingAndCount_success() {
        MentorSubjectReview review1 = MentorSubjectReview.builder().rating(5.0).build();
        MentorSubjectReview review2 = MentorSubjectReview.builder().rating(3.0).build();
        mentorSubject.setReviews(List.of(review1, review2));

        mentorSubjectService.updateRatingAndCount(mentorSubject);

        assertThat(mentorSubject.getReviewCount()).isEqualTo(2);
        assertThat(mentorSubject.getRating()).isEqualTo(4.0);
        verify(mentorSubjectRepository).save(mentorSubject);
    }

}