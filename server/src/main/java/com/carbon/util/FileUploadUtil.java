package com.carbon.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class FileUploadUtil {
    
    private static final String UPLOAD_DIR = "uploads";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif");
    
    /**
     * 上传文件
     */
    public static String uploadFile(MultipartFile file, String subDir) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }
        
        // 验证文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("文件大小不能超过5MB");
        }
        
        // 获取原始文件名和扩展名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new RuntimeException("文件名不能为空");
        }
        
        String extension = getFileExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new RuntimeException("只支持jpg、jpeg、png、gif格式的图片");
        }
        
        // 生成新文件名
        String newFilename = UUID.randomUUID().toString().replace("-", "") + "." + extension;
        
        // 构建文件路径：uploads/subDir/yyyy-MM-dd/filename
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String relativePath = subDir + File.separator + datePath;
        String fullPath = UPLOAD_DIR + File.separator + relativePath;
        
        // 创建目录
        Path directory = Paths.get(fullPath);
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }
        
        // 保存文件
        Path filePath = directory.resolve(newFilename);
        file.transferTo(filePath.toFile());
        
        // 返回相对路径URL
        return "/" + relativePath.replace(File.separator, "/") + "/" + newFilename;
    }
    
    /**
     * 获取文件扩展名
     */
    private static String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }
}
