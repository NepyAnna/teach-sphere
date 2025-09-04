package com.sheoanna.teach_sphere.review;

import com.sheoanna.teach_sphere.mentor_subject.MentorSubject;
import com.sheoanna.teach_sphere.mentor_subject.MentorSubjectService;
import com.sheoanna.teach_sphere.review.dtos.MentorSubjectReviewMapper;
import com.sheoanna.teach_sphere.review.dtos.MentorSubjectReviewRequest;
import com.sheoanna.teach_sphere.review.dtos.MentorSubjectReviewResponse;
import com.sheoanna.teach_sphere.review.exception.MentorSubjectReviewNotFoundException;
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
import org.springframework.security.access.AccessDeniedException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MentorSubjectReviewServiceTest {
    @Mock
    private MentorSubjectReviewRepository reviewRepository;
    @Mock
    private MentorSubjectReviewMapper reviewMapper;
    @Mock
    private UserService userService;
    @Mock
    private MentorSubjectService mentorSubjectService;

    @InjectMocks
    private MentorSubjectReviewService reviewService;

    private User student;
    private User anotherStudent;
    private MentorSubject mentorSubject;
    private MentorSubjectReview review;
    private MentorSubjectReviewRequest request;
    private MentorSubjectReviewResponse response;

    @BeforeEach
    void setUp(){
        student = User.builder().id(1L).roles(Set.of(Role.STUDENT)).build();
        anotherStudent = User.builder().id(2L).roles(Set.of(Role.STUDENT)).build();
        mentorSubject = MentorSubject.builder().id(2L).build();
        review = MentorSubjectReview.builder()
                .id(1L)
                .reviewer(student)
                .body("Nice")
                .rating(5.0)
                .build();
        request = new MentorSubjectReviewRequest(5.0, "Nice!", 2L);
        response = new MentorSubjectReviewResponse(1L, 5.0, "Nice!", LocalDateTime.now(), "student");
    }

    @Test
    void findAllReviews_success() {
        when(reviewRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(review)));
        when(reviewMapper.toResponse(review)).thenReturn(response);

        Page<MentorSubjectReviewResponse> result = reviewService.findAllReviews(PageRequest.of(0, 2));

        assertEquals(1, result.getTotalElements());
        assertThat(result.getContent().getFirst().body()).isEqualTo("Nice!");

        verify(reviewRepository).findAll(any(PageRequest.class));
        verify(reviewMapper).toResponse(review);
    }
    @Test
    void createReview_shouldSaveSuccess() {
        when(userService.getAuthenticatedUser()).thenReturn(student);
        when(mentorSubjectService.findByIdObj(2L)).thenReturn(mentorSubject);
        when(reviewMapper.toEntity(request)).thenReturn(review);
        when(reviewMapper.toResponse(review)).thenReturn(response);

        MentorSubjectReviewResponse result = reviewService.createReview(request);

        assertThat(result.body()).isEqualTo("Nice!");
        verify(reviewRepository).save(review);
        verify(mentorSubjectService).updateRatingAndCount(mentorSubject);
    }
    @Test
    void findReviewById_success() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(reviewMapper.toResponse(review)).thenReturn(response);

        MentorSubjectReviewResponse result = reviewService.findReviewById(1L);

        assertThat(result.body()).isEqualTo("Nice!");
        verify(reviewRepository).findById(1L);
    }
    @Test
    void updateReview_success() {
        MentorSubjectReviewRequest request =
                new MentorSubjectReviewRequest(5.0, "Updated", 2L);

        MentorSubjectReviewResponse response =
                new MentorSubjectReviewResponse(1L, 5.0, "Updated", LocalDateTime.now(), "student");

        when(userService.getAuthenticatedUser()).thenReturn(student);
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(mentorSubjectService.findByIdObj(2L)).thenReturn(mentorSubject);
        when(reviewMapper.toResponse(review)).thenReturn(response);

        MentorSubjectReviewResponse result = reviewService.updateReview(1L, request);

        assertThat(result.body()).isEqualTo("Updated");
        verify(mentorSubjectService).updateRatingAndCount(mentorSubject);
    }
    @Test
    void deleteReview_success() {
        MentorSubjectReview review = MentorSubjectReview.builder()
                .id(1L)
                .reviewer(student)
                .mentorSubject(mentorSubject)
                .build();

        when(userService.getAuthenticatedUser()).thenReturn(student);
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        reviewService.deleteReview(1L);

        verify(reviewRepository).delete(review);
        verify(mentorSubjectService).updateRatingAndCount(mentorSubject);
    }

    @Test
    void findReviewById_notFound() {
        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.findReviewById(99L))
                .isInstanceOf(MentorSubjectReviewNotFoundException.class)
                .hasMessageContaining("99");

        verify(reviewRepository).findById(99L);
    }

    @Test
    void createReview_notStudent() {
        User mentor = User.builder().id(1L).roles(Set.of(Role.MENTOR)).build();

        when(userService.getAuthenticatedUser()).thenReturn(mentor);

        assertThatThrownBy(() -> reviewService.createReview(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Only user-student allowed.");
    }
    @Test
    void updateReview_notOwner() {
        MentorSubjectReview review = MentorSubjectReview.builder()
                .id(1L)
                .reviewer(anotherStudent)
                .rating(3.0)
                .build();

        when(userService.getAuthenticatedUser()).thenReturn(student);
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        assertThatThrownBy(() -> reviewService.updateReview(1L, request))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("You are not allowed to modify or delete this mentor subject review.");
    }

    @Test
    void deleteReview_notOwner() {
        MentorSubjectReview review = MentorSubjectReview.builder()
                .id(1L)
                .reviewer(anotherStudent)
                .mentorSubject(MentorSubject.builder().id(10L).build())
                .build();

        when(userService.getAuthenticatedUser()).thenReturn(student);
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        assertThatThrownBy(() -> reviewService.deleteReview(1L))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("You are not allowed to modify or delete this mentor subject review.");
    }
}
