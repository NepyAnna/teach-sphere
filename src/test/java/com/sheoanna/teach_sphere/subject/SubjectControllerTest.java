package com.sheoanna.teach_sphere.subject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheoanna.teach_sphere.category.Category;
import com.sheoanna.teach_sphere.category.CategoryRepository;
import com.sheoanna.teach_sphere.subject.dtos.SubjectRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class SubjectControllerTest {

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
    private SubjectRepository subjectRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private final String BASE_URL = "/api/subjects";
    private Subject subject;
    private Category category;

    @BeforeEach
    void setup() {
        subjectRepository.deleteAll();
        categoryRepository.deleteAll();

        category = Category.builder().name("Math").build();
        categoryRepository.save(category);

        subject = Subject.builder().name("Algebra").category(category).build();
        subjectRepository.save(subject);
    }

    @Test
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void findAllSubjects_ReturnsPage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Algebra"));
    }

    @Test
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void findSubjectById_ReturnsSubject() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/" + subject.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Algebra"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createSubject_Success() throws Exception {
        SubjectRequest request = new SubjectRequest("Geometry", category.getId());

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Geometry"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateSubject_Success() throws Exception {
        SubjectRequest request = new SubjectRequest("Linear Algebra", category.getId());

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/" + subject.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Linear Algebra"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteSubject_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/" + subject.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void findSubjectById_NotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateSubject_NotFound() throws Exception {
        SubjectRequest request = new SubjectRequest("Physics", category.getId());

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteSubject_NotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/999"))
                .andExpect(status().isNotFound());
    }
}
