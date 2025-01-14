package com.example.restaurant_management_backend.services;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QRCodeServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(QRCodeServiceTest.class);

    @Test
    void testGenerateQRCodeImage_Success() {
        // Arrange
        QRCodeService qrCodeService = new QRCodeService();
        String testText = "https://example.com";

        // Act
        byte[] qrCodeImage = qrCodeService.generateQRCodeImage(testText);

        // Assert
        assertNotNull(qrCodeImage, "The generated QR code image should not be null.");
        assertTrue(qrCodeImage.length > 0, "The generated QR code image byte array should not be empty.");
    }
}