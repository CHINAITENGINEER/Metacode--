package com.huakang.service.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码哈希生成工具（仅用于生成测试数据）
 * 
 * @author huakang
 */
public class PasswordHashGenerator {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "123456";
        
        // 生成新的哈希值
        String hash = encoder.encode(password);
        System.out.println("===========================================");
        System.out.println("密码: " + password);
        System.out.println("新生成的BCrypt哈希值: " + hash);
        System.out.println("验证结果: " + encoder.matches(password, hash));
        System.out.println("===========================================");
        
        // 验证现有的哈希值
        String existingHash = "$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW";
        System.out.println("\n验证现有哈希值:");
        System.out.println("现有哈希值: " + existingHash);
        System.out.println("验证结果: " + encoder.matches(password, existingHash));
        System.out.println("===========================================");
        
        // 生成多个哈希值供选择
        System.out.println("\n生成5个不同的哈希值（BCrypt每次生成都不同，但都能验证同一个密码）:");
        for (int i = 1; i <= 5; i++) {
            String newHash = encoder.encode(password);
            boolean matches = encoder.matches(password, newHash);
            System.out.println(i + ". " + newHash + " (验证: " + matches + ")");
        }
    }
}
