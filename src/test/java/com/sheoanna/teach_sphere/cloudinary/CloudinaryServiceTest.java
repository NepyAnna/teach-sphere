package com.sheoanna.teach_sphere.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import java.io.IOException;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CloudinaryServiceTest {
    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Uploader uploader;

    @InjectMocks
    private CloudinaryService cloudinaryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cloudinaryService = new CloudinaryService(cloudinary);
        ReflectionTestUtils.setField(cloudinaryService,
                "defaultProfileImageUrl",
                "https://default-image.com/avatar.png");
        when(cloudinary.uploader()).thenReturn(uploader);
    }

    @Test
    void upload_Success() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "data".getBytes());
        Map<String, Object> mockResult = Map.of(
                "secure_url", "https://cloud.com/test.png",
                "public_id", "abc123"
        );

        when(uploader.upload(any(byte[].class), anyMap())).thenReturn(mockResult);

        UploadResult result = cloudinaryService.upload(file, "profiles");

        assertEquals("https://cloud.com/test.png", result.url());
        assertEquals("abc123", result.publicId());
        verify(uploader).upload(any(byte[].class), anyMap());
    }

    @Test
    void delete_Success() throws IOException {
        Map<String, Object> mockResult = Map.of("result", "ok");
        when(uploader.destroy(anyString(), anyMap())).thenReturn(mockResult);

        boolean deleted = cloudinaryService.delete("abc123");
        assertTrue(deleted);
        verify(uploader).destroy("abc123", Map.of());
    }

    @Test
    void delete_Failure() throws IOException {
        Map<String, Object> mockResult = Map.of("result", "not_ok");
        when(uploader.destroy(anyString(), anyMap())).thenReturn(mockResult);

        boolean deleted = cloudinaryService.delete("abc123");
        assertFalse(deleted);
    }

    @Test
    void delete_ThrowsException_OnIOException() throws IOException {
        when(uploader.destroy(anyString(), anyMap())).thenThrow(new IOException());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> cloudinaryService.delete("abc123"));
        assertTrue(ex.getMessage().contains("Failed to delete image from Cloudinary"));
    }

    @Test
    void uploadDefault_ReturnsDefaultUrl() {
        UploadResult result = cloudinaryService.uploadDefault("profiles");
        assertEquals("https://default-image.com/avatar.png", result.url());
        assertNull(result.publicId());
    }
}
