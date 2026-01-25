-- ============================================
-- 初始化测试账号
-- 说明：用于Day 3登录功能测试
-- ============================================

USE hk_electronics;

-- ============================================
-- 管理员账号（多个测试账号）
-- ============================================
-- 所有账号密码均为：123456
-- BCrypt哈希值（已验证，密码：123456）：$2a$10$kSTGesM41HcH9eCDnQMjNufEo65BW.5aZibC2KUfWh3eko1vRKOIi
-- 生成时间：2026-01-25（通过 PasswordHashGenerator.java 验证）

-- 注意：BCrypt每次生成的哈希值都不同（盐值随机），但都能验证同一个密码
-- 如果登录失败，请运行 PasswordHashGenerator.java 生成新的哈希值并更新此脚本

-- 1. 超级管理员
-- 使用已验证的BCrypt哈希值（密码：123456）
INSERT INTO admins (username, password_hash, name, email, phone, role, is_deleted) 
VALUES ('admin', '$2a$10$kSTGesM41HcH9eCDnQMjNufEo65BW.5aZibC2KUfWh3eko1vRKOIi', '系统管理员', 'admin@huakang.com', '13800000001', 'admin', 0)
ON DUPLICATE KEY UPDATE 
  password_hash = VALUES(password_hash),
  name = VALUES(name),
  email = VALUES(email),
  phone = VALUES(phone);

-- 2. 运营管理员
INSERT INTO admins (username, password_hash, name, email, phone, role, is_deleted) 
VALUES ('operator', '$2a$10$kSTGesM41HcH9eCDnQMjNufEo65BW.5aZibC2KUfWh3eko1vRKOIi', '运营管理员', 'operator@huakang.com', '13800000002', 'operator', 0)
ON DUPLICATE KEY UPDATE 
  password_hash = VALUES(password_hash),
  name = VALUES(name),
  email = VALUES(email),
  phone = VALUES(phone);

-- 3. 财务管理员
INSERT INTO admins (username, password_hash, name, email, phone, role, is_deleted) 
VALUES ('finance', '$2a$10$kSTGesM41HcH9eCDnQMjNufEo65BW.5aZibC2KUfWh3eko1vRKOIi', '财务管理员', 'finance@huakang.com', '13800000003', 'finance', 0)
ON DUPLICATE KEY UPDATE 
  password_hash = VALUES(password_hash),
  name = VALUES(name),
  email = VALUES(email),
  phone = VALUES(phone);

-- 4. 测试管理员（用于测试）
INSERT INTO admins (username, password_hash, name, email, phone, role, is_deleted) 
VALUES ('test', '$2a$10$kSTGesM41HcH9eCDnQMjNufEo65BW.5aZibC2KUfWh3eko1vRKOIi', '测试管理员', 'test@huakang.com', '13800000004', 'admin', 0)
ON DUPLICATE KEY UPDATE 
  password_hash = VALUES(password_hash),
  name = VALUES(name),
  email = VALUES(email),
  phone = VALUES(phone);

-- 5. 演示账号
INSERT INTO admins (username, password_hash, name, email, phone, role, is_deleted) 
VALUES ('demo', '$2a$10$kSTGesM41HcH9eCDnQMjNufEo65BW.5aZibC2KUfWh3eko1vRKOIi', '演示账号', 'demo@huakang.com', '13800000005', 'admin', 0)
ON DUPLICATE KEY UPDATE 
  password_hash = VALUES(password_hash),
  name = VALUES(name),
  email = VALUES(email),
  phone = VALUES(phone);

-- ============================================
-- 店员账号
-- ============================================
-- 用户名：staff001
-- 密码：123456
-- BCrypt哈希值已生成
INSERT INTO staffs (username, password_hash, name, status, is_deleted) 
VALUES ('staff001', '$2a$10$kSTGesM41HcH9eCDnQMjNufEo65BW.5aZibC2KUfWh3eko1vRKOIi', '店员A', 1, 0)
ON DUPLICATE KEY UPDATE 
  password_hash = VALUES(password_hash),
  name = VALUES(name),
  status = VALUES(status);

-- ============================================
-- 测试账号说明
-- ============================================
-- 所有账号密码均为：123456
-- 
-- 管理员账号列表：
-- 1. admin / 123456 - 系统管理员
-- 2. operator / 123456 - 运营管理员
-- 3. finance / 123456 - 财务管理员
-- 4. test / 123456 - 测试管理员
-- 5. demo / 123456 - 演示账号
--
-- 店员账号：
-- 1. staff001 / 123456 - 店员A
--
-- ============================================
-- 密码修改说明
-- ============================================
-- 如需修改密码，请使用以下Java代码生成新的哈希值：
-- 
-- BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
-- String hash = encoder.encode("新密码");
-- System.out.println(hash);
-- 
-- 然后将生成的哈希值更新到数据库对应账号的 password_hash 字段
