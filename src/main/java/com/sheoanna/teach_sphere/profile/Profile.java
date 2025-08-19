package com.sheoanna.teach_sphere.profile;

import com.sheoanna.teach_sphere.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="avatar_url")
    private String avatarUrl;

    @Column(name="bio")
    private String bio;

    @Column(name = "location", length = 50)
    private String location;


    @Column(name = "photo_public_id")
    private String avatarPublicId;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
