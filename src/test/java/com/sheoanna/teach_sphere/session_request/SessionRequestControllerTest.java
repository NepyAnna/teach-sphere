package com.sheoanna.teach_sphere.session_request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheoanna.teach_sphere.category.Category;
import com.sheoanna.teach_sphere.category.CategoryRepository;
import com.sheoanna.teach_sphere.mentor_subject.*;
import com.sheoanna.teach_sphere.session_request.dtos.SessionRequestRequest;
import com.sheoanna.teach_sphere.session_request.dtos.UpdateSessionStatusRequest;
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
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import java.util.Set;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
class SessionRequestControllerTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15-alpine"));

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
    private SubjectRepository subjectRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SessionRequestRepository sessionRepository;

    private User student;
    private User mentor;
    private MentorSubject mentorSubject;

    @BeforeEach
    void setUp() {
        sessionRepository.deleteAll();
        mentorSubjectRepository.deleteAll();
        subjectRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        student = User.builder().username("student").email("student@example.com").password("pass").roles(Set.of(Role.STUDENT)).build();
        student = userRepository.save(student);

        mentor = User.builder().username("mentor").email("mentor@example.com").password("pass").roles(Set.of(Role.MENTOR)).build();
        mentor = userRepository.save(mentor);

        Category category = Category.builder().name("Science").build();
        category = categoryRepository.save(category);

        Subject subject = Subject.builder().name("Math").category(category).build();
        subject = subjectRepository.save(subject);

        mentorSubject = MentorSubject.builder().mentor(mentor).subject(subject).rating(0.0).reviewCount(0).build();
        mentorSubject = mentorSubjectRepository.save(mentorSubject);
    }

    @Test
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void findRequestsForStudent_empty() throws Exception {
        mockMvc.perform(get("/api/session_requests/student"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    @WithMockUser(username = "mentor", roles = {"MENTOR"})
    void findRequestsForMentor_empty() throws Exception {
        mockMvc.perform(get("/api/session_requests/mentor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void createSessionRequest_success() throws Exception {
        SessionRequestRequest request = new SessionRequestRequest(mentorSubject.getId(), null);

        mockMvc.perform(post("/api/session_requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @WithMockUser(username = "mentor", roles = {"MENTOR"})
    void updateStatus_success() throws Exception {
        SessionRequest session = SessionRequest.builder()
                .mentorSubject(mentorSubject)
                .student(student)
                .status(RequestStatus.PENDING)
                .build();
        session = sessionRepository.save(session);

        UpdateSessionStatusRequest updateRequest = new UpdateSessionStatusRequest(RequestStatus.ACCEPTED);

        mockMvc.perform(put("/api/session_requests/{id}/status", session.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(session.getId()))
                .andExpect(jsonPath("$.requestStatus").value("ACCEPTED"));
    }
}
