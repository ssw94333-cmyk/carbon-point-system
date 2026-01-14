package com.carbon.util;

import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class PasswordUtil {
    
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int SALT_LENGTH = 16;
    
    /**
     * 加密密码
     */
    public String encode(String rawPassword) {
        try {
            // 生成盐值
            byte[] salt = new byte[SALT_LENGTH];
            RANDOM.nextBytes(salt);
            
            // 使用SHA-256加密
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(rawPassword.getBytes("UTF-8"));
            
            // 将盐值和加密后的密码组合
            byte[] combined = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hashedPassword, 0, combined, salt.length, hashedPassword.length);
            
            // Base64编码
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new RuntimeException("密码加密失败", e);
        }
    }
    
    /**
     * 验证密码
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        try {
            // Base64解码
            byte[] combined = Base64.getDecoder().decode(encodedPassword);
            
            // 提取盐值
            byte[] salt = new byte[SALT_LENGTH];
            System.arraycopy(combined, 0, salt, 0, SALT_LENGTH);
            
            // 提取加密后的密码
            byte[] hashedPassword = new byte[combined.length - SALT_LENGTH];
            System.arraycopy(combined, SALT_LENGTH, hashedPassword, 0, hashedPassword.length);
            
            // 使用相同的盐值加密输入的密码
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] testHash = md.digest(rawPassword.getBytes("UTF-8"));
            
            // 比较两个加密后的密码
            return MessageDigest.isEqual(hashedPassword, testHash);
        } catch (Exception e) {
            return false;
        }
    }
}
