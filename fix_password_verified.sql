-- ============================================
-- 修复密码哈希值（已验证版本）
-- 说明：使用已验证的 BCrypt 哈希值更新所有账号密码
-- 密码：123456
-- ============================================

USE hk_electronics;

-- 使用已验证的 BCrypt 哈希值（密码：123456）
-- 这个哈希值已经通过 PasswordHashGenerator.java 验证，可以匹配密码 "123456"
-- 生成时间：2026-01-25

-- 更新所有管理员账号的密码哈希值
UPDATE admins 
SET password_hash = '$2a$10$kSTGesM41HcH9eCDnQMjNufEo65BW.5aZibC2KUfWh3eko1vRKOIi',
    login_fail_count = 0,
    locked_until = NULL,
    updated_at = NOW()
WHERE is_deleted = 0;

-- 更新所有店员账号的密码哈希值
UPDATE staffs 
SET password_hash = '$2a$10$kSTGesM41HcH9eCDnQMjNufEo65BW.5aZibC2KUfWh3eko1vRKOIi',
    login_fail_count = 0,
    locked_until = NULL,
    updated_at = NOW()
WHERE is_deleted = 0;

-- 验证更新结果
SELECT username, name, login_fail_count, locked_until, 
       LEFT(password_hash, 20) as hash_prefix, updated_at
FROM admins 
WHERE is_deleted = 0;

SELECT username, name, login_fail_count, locked_until,
       LEFT(password_hash, 20) as hash_prefix, updated_at
FROM staffs 
WHERE is_deleted = 0;
