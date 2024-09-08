package com.example.restaurant_management_backend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/photos")
@Validated
public class PhotoController {

    private static final String UPLOAD_DIR = "uploads/";

    @PostMapping("/upload")
    @Operation(summary = "Upload a photo", description = "Upload a photo to the server")
    public ResponseEntity<String> uploadPhoto(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nie podano pliku");
        }

        try {
            // Define the path to save the file in the "uploads" folder at the root of the project
            String UPLOAD_DIR = new File("uploads").getAbsolutePath() + File.separator;

            // Create the directory for uploaded files if it doesn't exist
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                if (!uploadDir.mkdirs()) {  // Creates the directory if it doesn't exist
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Nie udało się utworzyć katalogu na pliki");
                }
            }

            // Save the file locally in the upload directory
            String filePath = UPLOAD_DIR + file.getOriginalFilename();
            file.transferTo(new File(filePath));

            // Return success response
            return ResponseEntity.ok("Plik został wgrany na serwer: " + filePath);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Błąd przy wgrywaniu pliku: " + e.getMessage());
        }
    }


    @GetMapping("/download")
    @Operation(summary = "Download a photo", description = "Download a photo from the server")
    public ResponseEntity<?> downloadPhoto(@RequestParam String filename) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nie znaleziono pliku o nazwie" + filename);
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)  // Set appropriate content type
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Błąd przy pobieraniu pliku: " + e.getMessage());
        }
    }

}
