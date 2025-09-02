package com.sheoanna.teach_sphere.review;

import com.sheoanna.teach_sphere.mentor_subject.MentorSubject;
import com.sheoanna.teach_sphere.mentor_subject.MentorSubjectService;
import com.sheoanna.teach_sphere.review.dtos.MentorSubjectReviewMapper;
import com.sheoanna.teach_sphere.review.dtos.MentorSubjectReviewRequest;
import com.sheoanna.teach_sphere.review.dtos.MentorSubjectReviewResponse;
import com.sheoanna.teach_sphere.review.exception.MentorSubjectReviewNotFoundException;
import com.sheoanna.teach_sphere.user.User;
import com.sheoanna.teach_sphere.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MentorSubjectReviewService {
    private final MentorSubjectReviewRepository reviewRepository;
    private final MentorSubjectReviewMapper reviewMapper;
    private final UserService userService;
    private final MentorSubjectService mentorSubjectService;

    public Page<MentorSubjectReviewResponse> findAllReviews(Pageable pageable) {
        return reviewRepository.findAll(pageable)
                .map(reviewMapper::toResponse);
    }

    public MentorSubjectReviewResponse findReviewById(Long id) {
        MentorSubjectReview existedReview = findByIdObj(id);
        return reviewMapper.toResponse(existedReview);
    }

    @Transactional
    public MentorSubjectReviewResponse createReview(MentorSubjectReviewRequest request) {
        User user = userService.getAuthenticatedUser();
        MentorSubject existedMentorSubject = mentorSubjectService.findByIdObj(request.mentorSubjectId());
        MentorSubjectReview newReview = reviewMapper.toEntity(request);

        newReview.setReviewer(user);
        newReview.setMentorSubject(existedMentorSubject);
        reviewRepository.save(newReview);

        return reviewMapper.toResponse(newReview);
    }

    @Transactional
    public MentorSubjectReviewResponse updateReview(Long id, MentorSubjectReviewRequest request) {
        User user = userService.getAuthenticatedUser();
        MentorSubjectReview existedReview = findByIdObj(id);

        checkCanModify(user, existedReview);
        MentorSubject mentorSubject = mentorSubjectService.findByIdObj(request.mentorSubjectId());

        existedReview.setBody(request.body().trim());
        existedReview.setRating(request.rating());
        existedReview.setReviewer(user);
        existedReview.setMentorSubject(mentorSubject);
        existedReview.setCreatedAt(LocalDateTime.now());

        return reviewMapper.toResponse(existedReview);
    }

    @Transactional
    public void deleteReview(Long id) {
        User user = userService.getAuthenticatedUser();
        MentorSubjectReview existedReview = findByIdObj(id);

        checkCanModify(user, existedReview);

        reviewRepository.delete(existedReview);
    }

    public MentorSubjectReview findByIdObj(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new MentorSubjectReviewNotFoundException(id));
    }

    public void checkCanModify(User user, MentorSubjectReview review) {
        if (!review.getReviewer().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not allowed to modify or delete this mentor subject review.");
        }
    }
}
