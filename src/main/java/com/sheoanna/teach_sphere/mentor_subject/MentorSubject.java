package com.sheoanna.teach_sphere.mentor_subject;

import com.sheoanna.teach_sphere.review.MentorSubjectReview;
import com.sheoanna.teach_sphere.session_request.SessionRequest;
import com.sheoanna.teach_sphere.subject.Subject;
import com.sheoanna.teach_sphere.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="mentor_subjects")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MentorSubject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private  Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User mentor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Subject subject;

    @Column(name = "rating")
    private double rating;

    @Column(name = "review_count")
    private int reviewCount;

    @OneToMany(mappedBy = "mentorSubject", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<MentorSubjectReview> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "mentorSubject", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<SessionRequest> sessionRequests = new ArrayList<>();
}
