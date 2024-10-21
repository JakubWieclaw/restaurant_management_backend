package com.example.restaurant_management_backend.controllers;

import com.example.restaurant_management_backend.services.QRCodeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/qr")
@Validated
@RequiredArgsConstructor
public class QRCodeController {

    private static final Logger logger = LoggerFactory.getLogger(QRCodeController.class);
    private final QRCodeService qrCodeService;

    @Operation(summary = "Get QR code for table", description = "Generates a QR code for a table")
    @GetMapping("/table/{id}")
    public ResponseEntity<byte[]> getQRCodeForOrder(@PathVariable("id") String id) {
        logger.info("Generating QR code for table with id: {}", id);
        byte[] qrCodeImage = qrCodeService.generateQRCodeImage("http://localhost:5173/tables/" + id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentLength(qrCodeImage.length);
        return new ResponseEntity<>(qrCodeImage, headers, HttpStatus.OK);
    }
}
