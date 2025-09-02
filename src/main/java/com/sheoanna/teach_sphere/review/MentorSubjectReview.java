package com.sheoanna.teach_sphere.review;

import com.sheoanna.teach_sphere.mentor_subject.MentorSubject;
import com.sheoanna.teach_sphere.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "mentor_subjects_reviews")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorSubjectReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private double rating;

    @NotNull
    private String body;

    @NotNull
    @PastOrPresent
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="mentor_subject_id", nullable = false)
    private MentorSubject mentorSubject;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User reviewer;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
