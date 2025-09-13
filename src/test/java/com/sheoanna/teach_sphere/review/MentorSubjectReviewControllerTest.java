package com.sheoanna.teach_sphere.review;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheoanna.teach_sphere.category.Category;
import com.sheoanna.teach_sphere.category.CategoryRepository;
import com.sheoanna.teach_sphere.mentor_subject.MentorSubject;
import com.sheoanna.teach_sphere.mentor_subject.MentorSubjectRepository;
import com.sheoanna.teach_sphere.review.dtos.MentorSubjectReviewRequest;
import com.sheoanna.teach_sphere.subject.Subject;
import com.sheoanna.teach_sphere.subject.SubjectRepository;
import com.sheoanna.teach_sphere.user.Role;
import com.sheoanna.teach_sphere.user.User;
import com.sheoanna.teach_sphere.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class MentorSubjectReviewControllerTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
    }
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MentorSubjectRepository mentorSubjectRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private MentorSubjectReviewRepository reviewRepository;

    private User student;
    private MentorSubject mentorSubject;

    @BeforeEach
    void setUp() {
        mentorSubjectRepository.deleteAll();
        subjectRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
        reviewRepository.deleteAll();

        student = User.builder()
                .username("student")
                .email("student@example.com")
                .password("password123")
                .roles(Set.of(Role.STUDENT))
                .build();
        student = userRepository.save(student);

        Category category = Category.builder().name("Science").build();
        category = categoryRepository.save(category);

        Subject subject = Subject.builder()
                .name("Math")
                .category(category)
                .build();
        subject = subjectRepository.save(subject);

        User mentor = User.builder()
                .username("mentor")
                .email("mentor@example.com")
                .password("password123")
                .roles(Set.of(Role.MENTOR))
                .build();
        mentor = userRepository.save(mentor);

        mentorSubject = MentorSubject.builder()
                .mentor(mentor)
                .subject(subject)
                .rating(0.0)
                .reviewCount(0)
                .build();
        mentorSubject = mentorSubjectRepository.save(mentorSubject);
    }

    @Test
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void findAll_success() throws Exception {
        mockMvc.perform(get("/api/mentor_subject_reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void createReview_success() throws Exception {
        MentorSubjectReviewRequest request = new MentorSubjectReviewRequest(
                5.0,
                "Great mentor!",
                2L
        );

        mockMvc.perform(post("/api/mentor_subject_reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.body").value("Great mentor!"))
                .andExpect(jsonPath("$.rating").value(5.0));
    }

    @Test
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void findReviewById_success() throws Exception {
        mockMvc.perform(get("/api/mentor_subject_reviews/{id}", 999L))
                .andExpect(status().isNotFound());
    }
}

