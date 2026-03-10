package com.library.controller;

import com.library.common.ForbiddenException;
import com.library.common.Result;
import com.library.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件控制器
 * 上传走 /api/upload（需JWT + 管理员权限）
 * 下载走 /api/files/**（公开访问，不经过JWT拦截器）
 */
@RestController
public class FileController {

    @Autowired
    private FileService fileService;

    /**
     * 上传文件（仅管理员，走JWT鉴权）
     */
    @PostMapping("/api/upload")
    public Result<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file,
                                                   HttpServletRequest httpReq) throws IOException {
        Integer role = (Integer) httpReq.getAttribute("role");
        if (role == null || role != 1) {
            throw new ForbiddenException("无权限操作");
        }
        String url = fileService.uploadFile(file);
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        return Result.success(result);
    }

    /**
     * 获取文件（公开访问，无需登录）
     * 使用 Files.probeContentType 自动检测 MIME 类型，覆盖 jpg/png/gif/webp/bmp/svg 等格式
     */
    @GetMapping("/api/files/{filename}")
    public ResponseEntity<byte[]> getFile(@PathVariable String filename) throws IOException {
        byte[] data = fileService.getFile(filename);

        // 自动探测 Content-Type，失败时按扩展名回退
        String contentType = null;
        try {
            contentType = Files.probeContentType(Paths.get(filename));
        } catch (IOException ignored) {
        }
        if (contentType == null) {
            contentType = guessContentType(filename);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(data);
    }

    private String guessContentType(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".png")) return MediaType.IMAGE_PNG_VALUE;
        if (lower.endsWith(".gif")) return MediaType.IMAGE_GIF_VALUE;
        if (lower.endsWith(".webp")) return "image/webp";
        if (lower.endsWith(".bmp")) return "image/bmp";
        if (lower.endsWith(".svg")) return "image/svg+xml";
        if (lower.endsWith(".ico")) return "image/x-icon";
        if (lower.endsWith(".pdf")) return MediaType.APPLICATION_PDF_VALUE;
        return MediaType.IMAGE_JPEG_VALUE; // 默认 JPEG
    }
}
