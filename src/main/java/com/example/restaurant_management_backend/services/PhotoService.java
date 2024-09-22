package com.example.restaurant_management_backend.services;

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

    public String uploadPhoto(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Nie podano pliku");
        }

        String uploadDirPath = new File(UPLOAD_DIR).getAbsolutePath();
        File uploadDir = new File(uploadDirPath);
        if (!uploadDir.exists() && !uploadDir.mkdirs()) {
            throw new IOException("Nie udało się utworzyć katalogu na pliki");
        }

        String filePath = uploadDirPath + File.separator + file.getOriginalFilename();
        file.transferTo(new File(filePath));

        return file.getOriginalFilename();
    }

    public Resource downloadPhoto(String filename) throws IOException {
        Path filePath = Paths.get(UPLOAD_DIR).resolve(filename).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            throw new IllegalArgumentException("Nie znaleziono pliku o nazwie " + filename);
        }

        return resource;
    }
}
