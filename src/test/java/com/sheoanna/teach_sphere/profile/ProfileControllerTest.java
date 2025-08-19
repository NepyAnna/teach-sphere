package com.sheoanna.teach_sphere.profile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheoanna.teach_sphere.cloudinary.CloudinaryService;
import com.sheoanna.teach_sphere.user.Role;
import com.sheoanna.teach_sphere.user.User;
import com.sheoanna.teach_sphere.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Set;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Transactional
class ProfileControllerTest {
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
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @MockitoBean
    private CloudinaryService cloudinaryService;

    private final String BASE_URL = "/api/profiles";

    private User user;

    @BeforeEach
    void setup() {
        user = User.builder()
                .username("testuser")
                .email("testemail@gmail.com")
                .password("encodedPassword")
                .roles(Set.of(Role.valueOf("STUDENT")))
                .build();

        Profile profile = Profile.builder()
                .bio("My bio")
                .location("Location")
                .user(user)
                .build();

        user.setProfile(profile);
        userRepository.save(user);
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testGetProfileById() throws Exception {
        Long profileId = user.getProfile().getId();

        mockMvc.perform(get(BASE_URL + "/" + profileId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bio").value("My bio"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testDeleteProfile() throws Exception {
        Long profileId = user.getProfile().getId();

        mockMvc.perform(delete(BASE_URL + "/" + profileId))
                .andExpect(status().isNoContent());
    }
}