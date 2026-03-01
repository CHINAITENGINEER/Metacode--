package com.huakang.admin;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码生成器 - 用于生成BCrypt密码哈希
 */
public class PasswordGenerator {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "123456";
        
        // 生成密码哈希
        String hash = encoder.encode(password);
        
        System.out.println("=================================");
        System.out.println("密码: " + password);
        System.out.println("BCrypt哈希: " + hash);
        System.out.println("=================================");
        
        // 验证密码
        boolean matches = encoder.matches(password, hash);
        System.out.println("验证结果: " + matches);
        
        // 测试之前的哈希值
        String oldHash = "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi";
        boolean oldMatches = encoder.matches(password, oldHash);
        System.out.println("旧哈希验证: " + oldMatches);
        
        // 生成SQL语句
        System.out.println("\n=================================");
        System.out.println("SQL语句:");
        System.out.println("UPDATE admins SET password_hash = '" + hash + "', login_fail_count = 0, locked_until = NULL WHERE username = 'admin1';");
        System.out.println("=================================");
    }
}
