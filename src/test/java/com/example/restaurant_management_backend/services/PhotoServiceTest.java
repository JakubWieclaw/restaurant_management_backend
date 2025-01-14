package com.example.restaurant_management_backend.services;

import com.example.restaurant_management_backend.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PhotoServiceTest {

    private PhotoService photoService;

    @BeforeEach
    void setUp() {
        photoService = new PhotoService();
    }

    @Test
    void testUploadPhoto_EmptyFile() {
        // Arrange
        MultipartFile file = new MockMultipartFile("empty", new byte[0]);

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> photoService.uploadPhoto(file));
        assertEquals(PhotoService.FILENAME_NOT_PRESENT, exception.getMessage());
    }

    @Test
    void testDownloadPhoto_FileNotFound() {
        // Arrange
        String fileName = "nonexistent.jpg";

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> photoService.downloadPhoto(fileName));
        assertEquals(PhotoService.NOT_FOUND_FILE_WITH_NAME + fileName, exception.getMessage());
    }
}
