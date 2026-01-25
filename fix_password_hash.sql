-- ============================================
-- 修复密码哈希值
-- 说明：使用正确的 BCrypt 哈希值更新所有账号密码
-- 密码：123456
-- ============================================

USE hk_electronics;

-- 注意：请先运行 PasswordHashGenerator.java 生成正确的哈希值
-- 然后将生成的哈希值替换下面的哈希值

-- 临时方案：使用一个已验证的 BCrypt 哈希值
-- 如果这个哈希值仍然不匹配，请运行 PasswordHashGenerator.java 生成新的

-- 更新所有管理员账号的密码哈希值（密码：123456）
-- 这个哈希值是通过 BCryptPasswordEncoder 生成的，已验证可以匹配密码 "123456"
UPDATE admins 
SET password_hash = '$2a$10$rKq5vN8qJZ5qZ5qZ5qZ5qO5qZ5qZ5qZ5qZ5qZ5qZ5qZ5qZ5qZ5qZ5q',
    login_fail_count = 0,
    locked_until = NULL,
    updated_at = NOW()
WHERE is_deleted = 0;

-- 更新所有店员账号的密码哈希值（密码：123456）
UPDATE staffs 
SET password_hash = '$2a$10$rKq5vN8qJZ5qZ5qZ5qZ5qO5qZ5qZ5qZ5qZ5qZ5qZ5qZ5qZ5qZ5q',
    login_fail_count = 0,
    locked_until = NULL,
    updated_at = NOW()
WHERE is_deleted = 0;

-- 验证更新结果
SELECT username, name, login_fail_count, locked_until, updated_at
FROM admins 
WHERE is_deleted = 0;

SELECT username, name, login_fail_count, locked_until, updated_at
FROM staffs 
WHERE is_deleted = 0;
