-- ============================================
-- 华康电器连锁积分小程序 - 企业级数据库初始化脚本（修复版）
-- 版本：v2.1
-- 说明：修复了表已存在时的字段问题，先删除再创建
-- ============================================

-- 1. 创建数据库
CREATE DATABASE IF NOT EXISTS hk_electronics 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE hk_electronics;

-- ============================================
-- 2. 删除已存在的表和视图（如果存在）
-- ============================================

-- 删除视图
DROP VIEW IF EXISTS `v_member_points_stats`;
DROP VIEW IF EXISTS `v_dashboard_stats`;

-- 删除存储过程
DROP PROCEDURE IF EXISTS `sp_check_points_consistency`;

-- 删除表（按依赖关系顺序）
DROP TABLE IF EXISTS `points_records`;
DROP TABLE IF EXISTS `products`;
DROP TABLE IF EXISTS `members`;
DROP TABLE IF EXISTS `system_configs`;
DROP TABLE IF EXISTS `staffs`;
DROP TABLE IF EXISTS `admins`;

-- ============================================
-- 3. 创建管理员表（基础表，无依赖）
-- ============================================
CREATE TABLE `admins` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` VARCHAR(50) NOT NULL COMMENT '登录账号',
  `password_hash` VARCHAR(255) NOT NULL COMMENT '密码哈希值（BCrypt）',
  `name` VARCHAR(100) NOT NULL COMMENT '管理员姓名',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `role` VARCHAR(20) NOT NULL DEFAULT 'admin' COMMENT '角色（预留）',
  `last_login_at` DATETIME DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` VARCHAR(50) DEFAULT NULL COMMENT '最后登录IP',
  `login_fail_count` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '连续登录失败次数',
  `locked_until` DATETIME DEFAULT NULL COMMENT '账号锁定到期时间',
  `is_deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除：0=否，1=是',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_is_deleted` (`is_deleted`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员表';

-- ============================================
-- 4. 创建店员表（基础表，无依赖）
-- ============================================
CREATE TABLE `staffs` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` VARCHAR(50) NOT NULL COMMENT '登录账号',
  `password_hash` VARCHAR(255) NOT NULL COMMENT '密码哈希值（BCrypt）',
  `name` VARCHAR(100) NOT NULL COMMENT '店员姓名',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `department` VARCHAR(50) DEFAULT NULL COMMENT '部门（预留）',
  `status` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态：1=启用，0=禁用',
  `last_login_at` DATETIME DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` VARCHAR(50) DEFAULT NULL COMMENT '最后登录IP',
  `login_fail_count` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '连续登录失败次数',
  `locked_until` DATETIME DEFAULT NULL COMMENT '账号锁定到期时间',
  `is_deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除：0=否，1=是',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_status` (`status`),
  KEY `idx_is_deleted` (`is_deleted`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='店员表';

-- ============================================
-- 5. 创建系统配置表（基础表，无依赖）
-- ============================================
CREATE TABLE `system_configs` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
  `config_value` TEXT COMMENT '配置值',
  `config_type` VARCHAR(20) NOT NULL DEFAULT 'string' COMMENT '配置类型：string/number/json/boolean',
  `config_group` VARCHAR(50) DEFAULT 'default' COMMENT '配置分组',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '配置说明',
  `version` INT UNSIGNED NOT NULL DEFAULT 1 COMMENT '配置版本号',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`),
  KEY `idx_config_group` (`config_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- ============================================
-- 6. 创建会员表（业务核心表）
-- ============================================
CREATE TABLE `members` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `openid` VARCHAR(64) NOT NULL COMMENT '微信OpenID',
  `nickname` VARCHAR(100) DEFAULT NULL COMMENT '微信昵称',
  `avatar` VARCHAR(500) DEFAULT NULL COMMENT '微信头像URL',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `total_points` INT NOT NULL DEFAULT 0 COMMENT '当前积分总额',
  `is_deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除：0=否，1=是',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_openid` (`openid`),
  KEY `idx_phone` (`phone`),
  KEY `idx_is_deleted` (`is_deleted`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员表';

-- ============================================
-- 7. 创建商品表
-- ============================================
CREATE TABLE `products` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(200) NOT NULL COMMENT '商品名称',
  `image` VARCHAR(500) DEFAULT NULL COMMENT '商品主图URL',
  `detail_images` JSON DEFAULT NULL COMMENT '商品详情图（JSON数组）',
  `category` VARCHAR(50) DEFAULT NULL COMMENT '商品分类',
  `type` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '商品类型：1=全量商品，2=积分商品',
  `price` DECIMAL(10,2) DEFAULT NULL COMMENT '商品价格（元）',
  `points_price` INT DEFAULT NULL COMMENT '积分价格',
  `description` TEXT COMMENT '商品描述',
  `status` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态：1=上架，0=下架',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序权重',
  `is_deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除：0=否，1=是',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_type_status_deleted` (`type`, `status`, `is_deleted`),
  KEY `idx_category` (`category`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

-- ============================================
-- 8. 创建积分记录表（核心流水表，最重要）
-- ============================================
CREATE TABLE `points_records` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `member_id` BIGINT UNSIGNED NOT NULL COMMENT '会员ID',
  `change_type` VARCHAR(50) NOT NULL COMMENT '变动类型：消费赠送/积分兑换/后台调整/新会员注册/积分扣除/积分过期',
  `points` INT NOT NULL COMMENT '变动分值（正数=增加，负数=扣除）',
  `balance_before` INT NOT NULL COMMENT '变动前积分余额',
  `balance_after` INT NOT NULL COMMENT '变动后积分余额',
  `business_no` VARCHAR(64) DEFAULT NULL COMMENT '业务单号（如订单号、兑换单号，便于对账）',
  `product_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '关联商品ID（积分兑换时）',
  `operator_type` VARCHAR(20) NOT NULL COMMENT '操作人类型：admin/staff/system',
  `operator_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '操作人ID（管理员或店员ID）',
  `operator_name` VARCHAR(100) DEFAULT NULL COMMENT '操作人姓名（冗余字段，便于查询）',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注/原因',
  `ip_address` VARCHAR(50) DEFAULT NULL COMMENT '操作IP地址',
  `user_agent` VARCHAR(500) DEFAULT NULL COMMENT '用户代理（设备信息）',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_member_created` (`member_id`, `created_at` DESC),
  KEY `idx_operator_created` (`operator_type`, `operator_id`, `created_at` DESC),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_change_type` (`change_type`),
  KEY `idx_business_no` (`business_no`),
  KEY `idx_product_id` (`product_id`),
  CONSTRAINT `fk_points_records_member` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_points_records_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分记录表';

-- ============================================
-- 9. 初始化系统配置数据
-- ============================================
INSERT INTO `system_configs` (`config_key`, `config_value`, `config_type`, `config_group`, `description`) 
VALUES 
('wechat_qrcode', '', 'string', 'wechat', '企业微信咨询二维码URL'),
('points_register_gift', '100', 'number', 'points', '新会员注册赠送积分'),
('points_consumption_ratio', '0.01', 'number', 'points', '消费积分比例（1元=0.01积分）')
ON DUPLICATE KEY UPDATE 
  `description` = VALUES(`description`),
  `config_type` = VALUES(`config_type`),
  `config_group` = VALUES(`config_group`);

-- ============================================
-- 10. 创建视图（便于统计查询）
-- ============================================

-- 会员积分统计视图
CREATE VIEW `v_member_points_stats` AS
SELECT 
    m.id,
    m.nickname,
    m.phone,
    m.total_points AS current_points,
    COALESCE(SUM(CASE WHEN pr.points > 0 THEN pr.points ELSE 0 END), 0) AS total_earned,
    COALESCE(SUM(CASE WHEN pr.points < 0 THEN ABS(pr.points) ELSE 0 END), 0) AS total_spent,
    COUNT(pr.id) AS record_count,
    MAX(pr.created_at) AS last_points_time
FROM members m
LEFT JOIN points_records pr ON m.id = pr.member_id
WHERE m.is_deleted = 0
GROUP BY m.id, m.nickname, m.phone, m.total_points;

-- 数据大屏统计视图
CREATE VIEW `v_dashboard_stats` AS
SELECT 
    (SELECT COUNT(*) FROM members WHERE is_deleted = 0) AS total_members,
    (SELECT COALESCE(SUM(points), 0) FROM points_records WHERE points > 0) AS total_points_issued,
    (SELECT COALESCE(SUM(ABS(points)), 0) FROM points_records WHERE points < 0) AS total_points_consumed,
    (SELECT COALESCE(SUM(total_points), 0) FROM members WHERE is_deleted = 0) AS current_points_pool;

-- ============================================
-- 11. 创建存储过程（数据一致性检查）
-- ============================================
CREATE PROCEDURE `sp_check_points_consistency`()
BEGIN
    -- 检查会员积分总额与积分记录是否一致
    SELECT
        m.id AS member_id,
        m.nickname,
        m.total_points AS db_points,
        COALESCE(MAX(pr.balance_after), 0) AS calculated_points,
        CASE 
            WHEN m.total_points = COALESCE(MAX(pr.balance_after), 0) THEN '一致'
            ELSE '不一致'
        END AS status
    FROM members m
    LEFT JOIN points_records pr ON m.id = pr.member_id
    WHERE m.is_deleted = 0
    GROUP BY m.id, m.nickname, m.total_points
    HAVING m.total_points != COALESCE(MAX(pr.balance_after), 0);
END;

-- ============================================
-- 12. 插入示例数据（可选）
-- ============================================
INSERT INTO `products` (`name`, `image`, `category`, `type`, `price`, `points_price`, `status`, `sort_order`)
VALUES
('品牌8K超高清智能电视75英寸', 'https://example.com/tv.jpg', '电视', 1, 8999.00, NULL, 1, 100),
('智能变频对开门冰箱580L', 'https://example.com/fridge.jpg', '冰箱', 1, 5999.00, NULL, 1, 90),
('全自动滚筒洗衣机10kg', 'https://example.com/washer.jpg', '洗衣机', 1, 3999.00, NULL, 1, 80),
('积分商品-精美礼品盒', 'https://example.com/gift.jpg', '礼品', 2, NULL, 500, 1, 70),
('积分商品-定制马克杯', 'https://example.com/mug.jpg', '礼品', 2, NULL, 200, 1, 60);

-- ============================================
-- 初始化完成！
-- ============================================
SELECT '数据库初始化完成！' AS message;
