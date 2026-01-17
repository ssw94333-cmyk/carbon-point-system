package com.carbon.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "aliyun.oss")
public class OssConfig {
    
    /**
     * OSS访问端点（Endpoint）
     * 例如：https://oss-cn-hangzhou.aliyuncs.com
     */
    private String endpoint;
    
    /**
     * 访问密钥ID（AccessKey ID）
     */
    private String accessKeyId;
    
    /**
     * 访问密钥（AccessKey Secret）
     */
    private String accessKeySecret;
    
    /**
     * 存储空间名称（Bucket Name）
     */
    private String bucketName;
    
    /**
     * 文件存储目录前缀
     * 例如：carbon-system/
     */
    private String prefix;
    
    /**
     * 自定义域名（可选）
     * 如果配置了自定义域名，则使用自定义域名访问文件
     * 否则使用默认的OSS域名
     */
    private String customDomain;
    
    /**
     * 是否启用OSS
     * true: 使用OSS存储
     * false: 使用本地存储
     */
    private Boolean enabled = true;
}
