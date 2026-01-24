-- ============================================
-- 华康电器连锁积分小程序 - 数据库初始化脚本
-- ============================================

-- 1. 创建数据库
CREATE DATABASE IF NOT EXISTS hk_electronics 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE hk_electronics;

-- ============================================
-- 2. 创建会员表
-- ============================================
CREATE TABLE IF NOT EXISTS `members` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `openid` VARCHAR(64) NOT NULL COMMENT '微信OpenID',
  `nickname` VARCHAR(100) DEFAULT NULL COMMENT '微信昵称',
  `avatar` VARCHAR(500) DEFAULT NULL COMMENT '微信头像URL',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `total_points` INT NOT NULL DEFAULT 0 COMMENT '当前积分总额',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_openid` (`openid`),
  KEY `idx_phone` (`phone`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员表';

-- ============================================
-- 3. 创建商品表
-- ============================================
CREATE TABLE IF NOT EXISTS `products` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(200) NOT NULL COMMENT '商品名称',
  `image` VARCHAR(500) DEFAULT NULL COMMENT '商品图片URL',
  `category` VARCHAR(50) DEFAULT NULL COMMENT '商品分类',
  `type` TINYINT NOT NULL DEFAULT 1 COMMENT '商品类型：1=全量商品，2=积分商品',
  `price` DECIMAL(10,2) DEFAULT NULL COMMENT '商品价格（元）',
  `points_price` INT DEFAULT NULL COMMENT '积分价格',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1=上架，0=下架',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序权重',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_type_status` (`type`, `status`),
  KEY `idx_category` (`category`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

-- ============================================
-- 4. 创建积分记录表
-- ============================================
CREATE TABLE IF NOT EXISTS `points_records` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `member_id` BIGINT UNSIGNED NOT NULL COMMENT '会员ID',
  `change_type` VARCHAR(50) NOT NULL COMMENT '变动类型',
  `points` INT NOT NULL COMMENT '变动分值（正数=增加，负数=扣除）',
  `operator_type` VARCHAR(20) NOT NULL COMMENT '操作人类型：admin/staff/system',
  `operator_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '操作人ID（管理员或店员ID）',
  `operator_name` VARCHAR(100) DEFAULT NULL COMMENT '操作人姓名（冗余字段，便于查询）',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注/原因',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_member_id` (`member_id`),
  KEY `idx_operator` (`operator_type`, `operator_id`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_change_type` (`change_type`),
  CONSTRAINT `fk_points_records_member` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分记录表';

-- ============================================
-- 5. 创建店员表
-- ============================================
CREATE TABLE IF NOT EXISTS `staffs` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` VARCHAR(50) NOT NULL COMMENT '登录账号',
  `password_hash` VARCHAR(255) NOT NULL COMMENT '密码哈希值',
  `name` VARCHAR(100) NOT NULL COMMENT '店员姓名',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1=启用，0=禁用',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='店员表';

-- ============================================
-- 6. 创建管理员表
-- ============================================
CREATE TABLE IF NOT EXISTS `admins` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` VARCHAR(50) NOT NULL COMMENT '登录账号',
  `password_hash` VARCHAR(255) NOT NULL COMMENT '密码哈希值',
  `name` VARCHAR(100) NOT NULL COMMENT '管理员姓名',
  `role` VARCHAR(20) NOT NULL DEFAULT 'admin' COMMENT '角色（预留）',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员表';

-- ============================================
-- 7. 创建系统配置表
-- ============================================
CREATE TABLE IF NOT EXISTS `system_configs` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
  `config_value` TEXT COMMENT '配置值',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '配置说明',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- ============================================
-- 8. 初始化系统配置数据
-- ============================================
INSERT INTO `system_configs` (`config_key`, `config_value`, `description`) 
VALUES 
('wechat_qrcode', '', '企业微信咨询二维码URL')
ON DUPLICATE KEY UPDATE `description` = VALUES(`description`);

-- ============================================
-- 9. 创建默认管理员账号（密码：admin123）
-- 注意：实际使用时，密码需要使用BCrypt加密后存储
-- 示例哈希值（BCrypt，cost=10）：$2b$10$rOzJ8K8qK8qK8qK8qK8qK8qK8qK8qK8qK8qK8qK8qK8qK8qK8qK
-- 请在生产环境中替换为实际加密后的密码
-- ============================================
-- INSERT INTO `admins` (`username`, `password_hash`, `name`) 
-- VALUES ('admin', '$2b$10$rOzJ8K8qK8qK8qK8qK8qK8qK8qK8qK8qK8qK8qK8qK8qK8qK8qK', '系统管理员')
-- ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- ============================================
-- 10. 插入测试数据（可选，仅用于开发测试）
-- ============================================

-- 测试商品数据
INSERT INTO `products` (`name`, `image`, `category`, `type`, `price`, `points_price`, `status`) 
VALUES 
('品牌8K超高清智能电视75英寸', 'https://example.com/tv.jpg', '电视', 1, 8999.00, NULL, 1),
('智能变频对开门冰箱580L', 'https://example.com/fridge.jpg', '冰箱', 1, 4599.00, NULL, 1),
('新款静音全自动滚筒洗衣机10KG', 'https://example.com/washer.jpg', '洗衣机', 1, 2199.00, NULL, 1),
('一级能效3匹变频立式空调', 'https://example.com/ac.jpg', '空调', 1, 6999.00, NULL, 1),
('高端智能电饭煲 5L', 'https://example.com/ricecooker.jpg', '厨房电器', 2, NULL, 5000, 1)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- ============================================
-- 11. 创建视图（可选，便于统计查询）
-- ============================================

-- 会员积分统计视图
CREATE OR REPLACE VIEW `v_member_points_stats` AS
SELECT 
    m.id,
    m.nickname,
    m.phone,
    m.total_points,
    COALESCE(SUM(CASE WHEN pr.points > 0 THEN pr.points ELSE 0 END), 0) AS total_earned,
    COALESCE(SUM(CASE WHEN pr.points < 0 THEN ABS(pr.points) ELSE 0 END), 0) AS total_spent,
    COUNT(pr.id) AS record_count
FROM members m
LEFT JOIN points_records pr ON m.id = pr.member_id
GROUP BY m.id, m.nickname, m.phone, m.total_points;

-- ============================================
-- 初始化完成
-- ============================================
