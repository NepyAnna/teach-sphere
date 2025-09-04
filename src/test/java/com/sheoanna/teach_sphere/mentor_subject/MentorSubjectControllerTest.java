package com.sheoanna.teach_sphere.mentor_subject;

import com.sheoanna.teach_sphere.category.Category;
import com.sheoanna.teach_sphere.category.CategoryRepository;
import com.sheoanna.teach_sphere.mentor_subject.dtos.MentorSubjectRequest;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class MentorSubjectControllerTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("test_teach_db")
            .withUsername("testuser")
            .withPassword("testpass");

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
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private MentorSubjectRepository mentorSubjectRepository;

    private User mentor;
    private Category category;
    private Subject subject;
    private MentorSubject mentorSubject;

    @BeforeEach
    void setUp() {
        mentorSubjectRepository.deleteAll();
        subjectRepository.deleteAll();
        userRepository.deleteAll();
        categoryRepository.deleteAll();

        mentor = userRepository.save(User.builder()
                .username("mentor")
                .email("mentor@example.com")
                .password("password123")
                .roles(Set.of(Role.MENTOR))
                .build());

        category = categoryRepository.save(Category.builder()
                .name("Science")
                .build());

        subject = subjectRepository.save(Subject.builder()
                .name("Math")
                .category(category)
                .build());

        mentorSubject = mentorSubjectRepository.save(MentorSubject.builder()
                .mentor(mentor)
                .subject(subject)
                .rating(0.0)
                .reviewCount(0)
                .build());
    }

    @Test
    @WithMockUser(username = "mentor", roles = {"MENTOR"})
    void findAllMentorSubjects_success() throws Exception {
        mockMvc.perform(get("/api/mentor_subjects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(mentorSubject.getId()))
                .andExpect(jsonPath("$.content[0].mentorName").value("mentor"))
                .andExpect(jsonPath("$.content[0].subjectName").value("Math"))
                .andExpect(jsonPath("$.content[0].rating").value(0.0))
                .andExpect(jsonPath("$.content[0].count").value(0));
    }

    @Test
    @WithMockUser(username = "mentor", roles = {"MENTOR"})
    void findMentorSubjectById_success() throws Exception {
        mockMvc.perform(get("/api/mentor_subjects/{id}", mentorSubject.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mentorSubject.getId()))
                .andExpect(jsonPath("$.mentorName").value("mentor"))
                .andExpect(jsonPath("$.subjectName").value("Math"))
                .andExpect(jsonPath("$.rating").value(0.0))
                .andExpect(jsonPath("$.count").value(0));
    }

    @Test
    @WithMockUser(username = "mentor", roles = {"MENTOR"})
    void createMentorSubject_success() throws Exception {
        MentorSubjectRequest request = new MentorSubjectRequest(subject.getId());

        mockMvc.perform(post("/api/mentor_subjects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.mentorName").value("mentor"))
                .andExpect(jsonPath("$.subjectName").value("Math"))
                .andExpect(jsonPath("$.rating").value(0.0))
                .andExpect(jsonPath("$.count").value(0));
    }

    @Test
    @WithMockUser(username = "mentor", roles = {"MENTOR"})
    void deleteMentorSubject_WhenExists() throws Exception {
        mockMvc.perform(delete("/api/mentor_subjects/{id}", mentorSubject.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "mentor", roles = {"MENTOR"})
    void findMentorSubject_NotFound() throws Exception {
        mockMvc.perform(get("/api/mentor_subjects/{id}", 999L))
                .andExpect(status().isNotFound());
    }
}