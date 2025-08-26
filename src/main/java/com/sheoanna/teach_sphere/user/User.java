package com.sheoanna.teach_sphere.user;

import com.sheoanna.teach_sphere.mentor_subject.MentorSubject;
import com.sheoanna.teach_sphere.profile.Profile;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name ="users")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="username", nullable = false, unique = true, length = 30)
    private String username;

    @Column(name="email", nullable = false, unique = true, length = 30)
    private String email;

    @Column(name="password", nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name="role")
    private Set<Role> roles = new HashSet<>();

    @OneToOne(mappedBy = "user")
    private Profile profile;

    @OneToMany(mappedBy = "mentor")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<MentorSubject> mentorSubjects = new ArrayList<>();

/* @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SessionRequest> sentRequests = new ArrayList<>();*/
}