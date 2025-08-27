package com.sheoanna.teach_sphere.profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheoanna.teach_sphere.cloudinary.CloudinaryService;
import com.sheoanna.teach_sphere.cloudinary.UploadResult;
import com.sheoanna.teach_sphere.user.Role;
import com.sheoanna.teach_sphere.user.User;
import com.sheoanna.teach_sphere.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
        profileRepository.deleteAll();
        userRepository.deleteAll();

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
        profileRepository.save(profile);
        userRepository.save(user);
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"STUDENT"})
    void testGetProfileById() throws Exception {
        Long profileId = user.getProfile().getId();

        mockMvc.perform(get(BASE_URL + "/" + profileId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bio").value("My bio"));
    }

    @Test
    @WithMockUser(username = "nonexistent", roles = {"STUDENT"})
    void getProfileById_NotFound() throws Exception {
        Long nonExistentId = 999L;

        mockMvc.perform(get(BASE_URL + "/" + nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Profile with ID " + nonExistentId + " not found."));
    }

    @Test
    @WithMockUser(username = "newuser", roles = {"STUDENT"})
    void createProfile_Success() throws Exception {
        User newUser = User.builder()
                .username("newuser")
                .email("newuseremail@gmail.com")
                .password("encodedPassword")
                .roles(Set.of(Role.STUDENT))
                .build();
        userRepository.save(newUser);
        UploadResult mockResult = new UploadResult("mock-url.com/avatar.png", "mock_public_id");

        when(cloudinaryService.upload(any(), anyString())).thenReturn(mockResult);

        MockMultipartFile avatar = new MockMultipartFile("avatar", "avatar.png", "image/png", "image-content".getBytes());

        mockMvc.perform(multipart("/api/profiles")
                        .file(avatar)
                        .param("bio", "New bio")
                        .param("location", "Kyiv")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bio").value("New bio"))
                .andExpect(jsonPath("$.location").value("Kyiv"))
                .andExpect(jsonPath("$.avatarUrl").value("mock-url.com/avatar.png"));
    }

    @WithMockUser(username = "testuser", roles = {"STUDENT"})
    void createProfile_AlreadyExists() throws Exception {
        UploadResult mockResult = new UploadResult("mock-url.com/avatar.png", "mock_public_id");

        when(cloudinaryService.upload(any(), anyString())).thenReturn(mockResult);

        MockMultipartFile avatar = new MockMultipartFile("avatar", "avatar.png", "image/png", "image-content".getBytes());

        mockMvc.perform(multipart("/api/profiles")
                        .file(avatar)
                        .param("bio", "New bio")
                        .param("location", "Kyiv")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isConflict())
                .andExpect(content().string("Profile for user with ID " + user.getId() + " already exists!"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"STUDENT"})
    void testDeleteProfile() throws Exception {
        Long profileId = user.getProfile().getId();

        mockMvc.perform(delete(BASE_URL + "/" + profileId))
                .andExpect(status().isNoContent());
    }
}