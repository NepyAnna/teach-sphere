package com.sheoanna.teach_sphere.mentor_subject;

import com.sheoanna.teach_sphere.subject.Subject;
import com.sheoanna.teach_sphere.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private User mentor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

/*    @OneToMany(mappedBy = "mentorSubject", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "mentorSubject", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SessionRequest> sessionRequests = new ArrayList<>();*/
}
