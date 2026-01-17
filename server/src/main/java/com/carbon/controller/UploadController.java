package com.carbon.controller;

import com.carbon.common.Result;
import com.carbon.util.OssUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/upload")
public class UploadController {
    
    @Autowired
    private OssUtil ossUtil;
    
    /**
     * 通用文件上传接口
     * 支持根据fileType参数上传到不同目录
     */
    @PostMapping("/file")
    public Result<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "fileType", defaultValue = "common") String fileType) {
        try {
            // 1. 校验文件
            if (file.isEmpty()) {
                return Result.error("文件不能为空");
            }
            
            // 2. 校验文件类型（图片）
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return Result.error("只支持图片格式");
            }
            
            // 3. 校验文件大小（2MB）
            if (file.getSize() > 2 * 1024 * 1024) {
                return Result.error("文件大小不能超过 2MB");
            }
            
            // 4. 根据fileType确定上传目录
            String folder;
            switch (fileType.toLowerCase()) {
                case "behavior":
                    folder = "behavior";
                    break;
                case "avatar":
                    folder = "avatars";
                    break;
                case "product":
                    folder = "product";
                    break;
                case "activity":
                    folder = "activity";
                    break;
                default:
                    folder = "common";
                    break;
            }
            
            // 5. 上传文件
            String url = ossUtil.uploadFile(file, folder);
            
            // 6. 返回URL（包装在data对象中）
            Map<String, String> data = new java.util.HashMap<>();
            data.put("url", url);
            return Result.success("上传成功", data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 上传用户头像
     * 限制：只支持JPG/PNG格式，最大2MB
     */
    @PostMapping("/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            // 1. 校验文件
            if (file.isEmpty()) {
                return Result.error("文件不能为空");
            }
            
            // 2. 校验文件类型
            String contentType = file.getContentType();
            if (!"image/jpeg".equals(contentType) && !"image/png".equals(contentType)) {
                return Result.error("只支持 JPG 和 PNG 格式");
            }
            
            // 3. 校验文件大小（2MB = 2 * 1024 * 1024 bytes）
            if (file.getSize() > 2 * 1024 * 1024) {
                return Result.error("文件大小不能超过 2MB");
            }
            
            // 4. 上传文件
            String url = ossUtil.uploadFile(file, "avatars");
            return Result.success("上传成功", url);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 上传行为证明材料
     */
    @PostMapping("/behavior-proof")
    public Result<String> uploadBehaviorProof(@RequestParam("file") MultipartFile file) {
        try {
            String url = ossUtil.uploadFile(file, "behavior-proof");
            return Result.success("上传成功", url);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 上传商品图片
     */
    @PostMapping("/product-image")
    public Result<String> uploadProductImage(@RequestParam("file") MultipartFile file) {
        try {
            String url = ossUtil.uploadFile(file, "product");
            return Result.success("上传成功", url);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 上传活动图片
     */
    @PostMapping("/activity-image")
    public Result<String> uploadActivityImage(@RequestParam("file") MultipartFile file) {
        try {
            String url = ossUtil.uploadFile(file, "activity");
            return Result.success("上传成功", url);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
