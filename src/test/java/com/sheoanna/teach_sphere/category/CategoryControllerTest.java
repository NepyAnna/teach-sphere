package com.sheoanna.teach_sphere.category;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheoanna.teach_sphere.category.dtos.CategoryRequest;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class CategoryControllerTest {
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
    private CategoryRepository categoryRepository;

    private Category testCategory;
    private CategoryRequest request;
    private final String BASE_URL = "/api/categories";

    @BeforeEach
    void setup() {
        categoryRepository.deleteAll();
        testCategory = Category.builder()
                .name("Test Category")
                .build();
        request = new CategoryRequest("New Category");
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"STUDENT"})
    void findAllCategories_ReturnsPage() throws Exception {
        categoryRepository.save(testCategory);

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createCategory_AsAdmin_ReturnsCreated() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void createCategory_AsStudent_Forbidden() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateCategory_AsAdmin_Successfully() throws Exception {
        Category category = categoryRepository.save(testCategory);
        CategoryRequest updateRequest = new CategoryRequest("Updated Name");

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/" + category.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(category.getId()))
                .andExpect(jsonPath("$.name").value("Updated Name"));

        Optional<Category> updated = categoryRepository.findById(category.getId());
        assertTrue(updated.isPresent());
        assertEquals("Updated Name", updated.get().getName());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteCategory_AsAdmin_DeletesSuccessfully() throws Exception {
        Category category = Category.builder().name("To Delete").build();
        category = categoryRepository.save(category);

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/" + category.getId()))
                .andExpect(status().isNoContent());

        assertFalse(categoryRepository.findById(category.getId()).isPresent());
    }

    @Test
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void updateCategory_AsStudent_Forbidden() throws Exception {
        Category category = Category.builder().name("Old Name").build();
        category = categoryRepository.save(category);
        CategoryRequest updateRequest = new CategoryRequest("Updated Name");

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/" + category.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = {"STUDENT"})
    void deleteCategory_AsStudent_Forbidden() throws Exception {
        Category category = categoryRepository.save(testCategory);

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/" + category.getId()))
                .andExpect(status().isForbidden());
    }
}