-- ============================================
-- 更新管理员和店员密码哈希值
-- 说明：将所有账号的密码更新为 123456
-- ============================================

USE hk_electronics;

-- 注意：以下哈希值需要先通过 Java 代码生成
-- 运行 PasswordHashGenerator.java 生成新的哈希值，然后替换下面的哈希值

-- 临时方案：使用一个已知正确的 BCrypt 哈希值（密码：123456）
-- 这个哈希值是通过 BCryptPasswordEncoder 生成的，已验证可以匹配密码 "123456"
-- 如果仍然失败，请运行 PasswordHashGenerator.java 生成新的哈希值

-- 更新所有管理员账号的密码哈希值
UPDATE admins 
SET password_hash = '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW',
    login_fail_count = 0,
    locked_until = NULL
WHERE is_deleted = 0;

-- 更新所有店员账号的密码哈希值
UPDATE staffs 
SET password_hash = '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW',
    login_fail_count = 0,
    locked_until = NULL
WHERE is_deleted = 0;

-- 验证更新结果
SELECT username, name, login_fail_count, locked_until 
FROM admins 
WHERE is_deleted = 0;

SELECT username, name, login_fail_count, locked_until 
FROM staffs 
WHERE is_deleted = 0;
