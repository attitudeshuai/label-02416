package com.library.service;

import com.library.common.BusinessException;
import com.library.common.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 文件上传服务
 * 包含路径穿越防护
 */
@Service
public class FileService {

    private static final Logger log = LoggerFactory.getLogger(FileService.class);

    @Value("${file.upload-path}")
    private String uploadPath;

    public String uploadFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        Path uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize();
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String newFilename = UUID.randomUUID().toString() + extension;

        Path filePath = uploadDir.resolve(newFilename).normalize();
        if (!filePath.startsWith(uploadDir)) {
            throw new BusinessException("非法文件路径");
        }

        file.transferTo(filePath.toFile());
        log.info("文件上传成功: filename={}, size={}", newFilename, file.getSize());
        return "/api/files/" + newFilename;
    }

    public byte[] getFile(String filename) throws IOException {
        Path uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize();
        Path filePath = uploadDir.resolve(filename).normalize();

        if (!filePath.startsWith(uploadDir)) {
            throw new BusinessException("非法文件路径");
        }

        if (!Files.exists(filePath)) {
            throw new ResourceNotFoundException("文件不存在");
        }
        return Files.readAllBytes(filePath);
    }
}
