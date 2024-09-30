package com.example.restaurant_management_backend.controllers;

import com.example.restaurant_management_backend.services.PhotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.StringToClassMapItem;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/photos")
@Validated
@RequiredArgsConstructor
public class PhotoController {
    private final PhotoService photoService;

    @PostMapping("/upload")
    @Operation(summary = "Upload a file", description = "Uploads an image file", requestBody = @RequestBody(content = @Content(mediaType = "multipart/form-data", schema = @Schema(type = "object", properties = {
            @StringToClassMapItem(key = "file", value = MultipartFile.class)
    }))), responses = {
            @ApiResponse(responseCode = "201", description = "File uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file")
    })
    public ResponseEntity<String> uploadPhoto(@RequestParam("file") MultipartFile file) throws IOException {
        String fileName = photoService.uploadPhoto(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(fileName);
    }

    @GetMapping("/download")
    @Operation(summary = "Download a photo", description = "Download a photo from the server")
    public ResponseEntity<Resource> downloadPhoto(@RequestParam String filename) throws IOException {
        Resource resource = photoService.downloadPhoto(filename);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }
}
