package com.example.restaurant_management_backend.services;

import com.example.restaurant_management_backend.exceptions.NotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class PhotoService {
    private static final String UPLOAD_DIR = "uploads/";
    public static final String FILENAME_NOT_PRESENT = "Nie podano pliku";
    public static final String DIR_NOT_CREATED_FOR_FILES = "Nie udało się utworzyć katalogu na pliki";
    public static final String NOT_FOUND_FILE_WITH_NAME = "Nie znaleziono pliku o nazwie ";

    public String uploadPhoto(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new NotFoundException(FILENAME_NOT_PRESENT);
        }

        String uploadDirPath = new File(UPLOAD_DIR).getAbsolutePath();
        File uploadDir = new File(uploadDirPath);
        if (!uploadDir.exists() && !uploadDir.mkdirs()) {
            throw new IOException(DIR_NOT_CREATED_FOR_FILES);
        }

        String filePath = uploadDirPath + File.separator + file.getOriginalFilename();
        file.transferTo(new File(filePath));

        return file.getOriginalFilename();
    }

    public Resource downloadPhoto(String filename) throws IOException {
        Path filePath = Paths.get(UPLOAD_DIR).resolve(filename).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            throw new NotFoundException(NOT_FOUND_FILE_WITH_NAME + filename);
        }

        return resource;
    }
}
