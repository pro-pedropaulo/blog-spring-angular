package com.example.blog.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.junit.jupiter.api.Assertions.*;

class CloudinaryServiceTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Uploader uploader;

    @InjectMocks
    private CloudinaryService cloudinaryService;

    @BeforeEach
    void setUp() throws Exception {
        openMocks(this);
        when(cloudinary.uploader()).thenReturn(uploader);
    }

    @Test
    void uploadFile_success() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("url", "http://example.com/test.jpg");

        when(uploader.upload(any(File.class), any(Map.class))).thenReturn(uploadResult);

        String result = cloudinaryService.uploadFile(file);

        assertNotNull(result);
        assertEquals("http://example.com/test.jpg", result);
    }

    @Test
    void uploadFile_failure() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());

        when(uploader.upload(any(File.class), any(Map.class))).thenThrow(new IOException());

        assertThrows(IOException.class, () -> cloudinaryService.uploadFile(file));
    }
}
