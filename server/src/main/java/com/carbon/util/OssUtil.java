package com.carbon.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import com.carbon.config.OssConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class OssUtil {
    
    @Autowired
    private OssConfig ossConfig;
    
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "webp");
    
    /**
     * 上传文件到OSS
     * 
     * @param file 文件
     * @param folder 文件夹名称（如：avatar、behavior-proof）
     * @return 文件访问URL
     */
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        // 检查OSS是否启用
        if (!ossConfig.getEnabled()) {
            log.warn("OSS未启用，使用本地存储");
            return FileUploadUtil.uploadFile(file, folder);
        }
        
        // 验证文件
        validateFile(file);
        
        // 生成文件名
        String fileName = generateFileName(file.getOriginalFilename(), folder);
        
        // 上传到OSS
        OSS ossClient = null;
        try {
            // 创建OSS客户端
            ossClient = new OSSClientBuilder().build(
                ossConfig.getEndpoint(),
                ossConfig.getAccessKeyId(),
                ossConfig.getAccessKeySecret()
            );
            
            // 设置文件元信息
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());
            metadata.setContentDisposition("inline"); // 浏览器直接显示
            
            // 上传文件
            InputStream inputStream = file.getInputStream();
            PutObjectResult result = ossClient.putObject(
                ossConfig.getBucketName(),
                fileName,
                inputStream,
                metadata
            );
            
            log.info("文件上传成功: {}", fileName);
            
            // 返回文件访问URL
            return getFileUrl(fileName);
            
        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
    
    /**
     * 删除OSS文件
     * 
     * @param fileUrl 文件URL
     */
    public void deleteFile(String fileUrl) {
        if (!ossConfig.getEnabled() || fileUrl == null || fileUrl.isEmpty()) {
            return;
        }
        
        OSS ossClient = null;
        try {
            // 从URL中提取文件名
            String fileName = extractFileNameFromUrl(fileUrl);
            if (fileName == null) {
                log.warn("无法从URL中提取文件名: {}", fileUrl);
                return;
            }
            
            // 创建OSS客户端
            ossClient = new OSSClientBuilder().build(
                ossConfig.getEndpoint(),
                ossConfig.getAccessKeyId(),
                ossConfig.getAccessKeySecret()
            );
            
            // 删除文件
            ossClient.deleteObject(ossConfig.getBucketName(), fileName);
            log.info("文件删除成功: {}", fileName);
            
        } catch (Exception e) {
            log.error("文件删除失败: {}", e.getMessage(), e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
    
    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }
        
        // 验证文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("文件大小不能超过5MB");
        }
        
        // 获取文件扩展名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new RuntimeException("文件名不能为空");
        }
        
        String extension = getFileExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new RuntimeException("只支持以下格式的图片: " + String.join(", ", ALLOWED_EXTENSIONS));
        }
    }
    
    /**
     * 生成文件名
     * 格式: prefix/folder/yyyy-MM-dd/uuid.ext
     */
    private String generateFileName(String originalFilename, String folder) {
        String extension = getFileExtension(originalFilename);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        StringBuilder fileName = new StringBuilder();
        
        // 添加前缀
        if (ossConfig.getPrefix() != null && !ossConfig.getPrefix().isEmpty()) {
            fileName.append(ossConfig.getPrefix());
            if (!ossConfig.getPrefix().endsWith("/")) {
                fileName.append("/");
            }
        }
        
        // 添加文件夹
        fileName.append(folder).append("/");
        
        // 添加日期路径
        fileName.append(datePath).append("/");
        
        // 添加文件名
        fileName.append(uuid).append(".").append(extension);
        
        return fileName.toString();
    }
    
    /**
     * 获取文件访问URL
     */
    private String getFileUrl(String fileName) {
        // 如果配置了自定义域名，使用自定义域名
        if (ossConfig.getCustomDomain() != null && !ossConfig.getCustomDomain().isEmpty()) {
            String domain = ossConfig.getCustomDomain();
            if (!domain.startsWith("http://") && !domain.startsWith("https://")) {
                domain = "https://" + domain;
            }
            if (!domain.endsWith("/")) {
                domain += "/";
            }
            return domain + fileName;
        }
        
        // 使用默认的OSS域名
        // 格式: https://bucket-name.endpoint/file-name
        String endpoint = ossConfig.getEndpoint();
        if (endpoint.startsWith("https://")) {
            endpoint = endpoint.substring(8);
        } else if (endpoint.startsWith("http://")) {
            endpoint = endpoint.substring(7);
        }
        
        return "https://" + ossConfig.getBucketName() + "." + endpoint + "/" + fileName;
    }
    
    /**
     * 从URL中提取文件名
     */
    private String extractFileNameFromUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return null;
        }
        
        try {
            // 如果是自定义域名
            if (ossConfig.getCustomDomain() != null && fileUrl.contains(ossConfig.getCustomDomain())) {
                int index = fileUrl.indexOf(ossConfig.getCustomDomain()) + ossConfig.getCustomDomain().length();
                if (fileUrl.charAt(index) == '/') {
                    index++;
                }
                return fileUrl.substring(index);
            }
            
            // 如果是OSS默认域名
            if (fileUrl.contains(ossConfig.getBucketName())) {
                int index = fileUrl.indexOf(ossConfig.getBucketName()) + ossConfig.getBucketName().length();
                String endpoint = ossConfig.getEndpoint();
                if (endpoint.startsWith("https://")) {
                    endpoint = endpoint.substring(8);
                } else if (endpoint.startsWith("http://")) {
                    endpoint = endpoint.substring(7);
                }
                index = fileUrl.indexOf(endpoint) + endpoint.length();
                if (fileUrl.charAt(index) == '/') {
                    index++;
                }
                return fileUrl.substring(index);
            }
            
            return null;
        } catch (Exception e) {
            log.error("提取文件名失败: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }
}
