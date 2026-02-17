package com.library.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * File Upload Service
 */
@Service
public class FileService {

    @Value("${file.upload-path}")
    private String uploadPath;

    public String uploadFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        // Create upload directory if not exists
        Path uploadDir = Paths.get(uploadPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String newFilename = UUID.randomUUID().toString() + extension;

        // Save file
        Path filePath = uploadDir.resolve(newFilename);
        file.transferTo(filePath.toFile());

        return "/api/files/" + newFilename;
    }

    public byte[] getFile(String filename) throws IOException {
        Path filePath = Paths.get(uploadPath).resolve(filename);
        if (!Files.exists(filePath)) {
            throw new RuntimeException("File not found");
        }
        return Files.readAllBytes(filePath);
    }
}
