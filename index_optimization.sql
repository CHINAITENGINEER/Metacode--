-- ============================================
-- 数据库索引优化脚本
-- 说明：根据业务查询场景添加必要的索引以提升查询性能
-- 执行前请先备份数据库！
-- ============================================

USE hk_electronics;

-- ============================================
-- 1. 会员表 (members) 索引优化
-- ============================================

-- 注意：members表没有last_login_at字段，该字段只在admins和staffs表中

-- 1.1 复合索引：软删除 + 创建时间（用于列表查询优化）
ALTER TABLE members ADD KEY `idx_deleted_created` (`is_deleted`, `created_at` DESC);

-- ============================================
-- 2. 商品表 (products) 索引优化
-- ============================================

-- 2.1 删除冗余索引（复合索引已包含，避免重复）
-- 注意：如果这些索引不存在，会报错，可以忽略
-- ALTER TABLE products DROP KEY `idx_status`;
-- ALTER TABLE products DROP KEY `idx_is_deleted`;

-- 2.2 全量商品列表排序优化（type + status + is_deleted + sort_order + id）
ALTER TABLE products ADD KEY `idx_type_status_sort` (`type`, `status`, `is_deleted`, `sort_order` DESC, `id` DESC);

-- 2.3 分类查询优化（category + type + status + is_deleted + sort_order）
ALTER TABLE products ADD KEY `idx_category_type_status` (`category`, `type`, `status`, `is_deleted`, `sort_order` DESC);

-- 2.4 积分商品按价格排序（type + status + is_deleted + points_price + sort_order）
ALTER TABLE products ADD KEY `idx_type_status_points` (`type`, `status`, `is_deleted`, `points_price` ASC, `sort_order` DESC);

-- ============================================
-- 3. 积分记录表 (points_records) 索引优化 ⚠️ 最重要
-- ============================================

-- 3.1 会员+变动类型+时间复合索引（用于筛选查询）
ALTER TABLE points_records ADD KEY `idx_member_type_created` (`member_id`, `change_type`, `created_at` DESC);

-- 3.2 时间范围+变动类型统计（用于数据大屏统计）
ALTER TABLE points_records ADD KEY `idx_created_type` (`created_at`, `change_type`);

-- 3.3 积分字段索引（用于统计查询：SUM(points) WHERE points > 0）
ALTER TABLE points_records ADD KEY `idx_points` (`points`);

-- ============================================
-- 4. 店员表 (staffs) 索引优化
-- ============================================

-- 4.1 复合索引：状态+软删除+创建时间（用于列表查询）
ALTER TABLE staffs ADD KEY `idx_status_deleted` (`status`, `is_deleted`, `created_at` DESC);

-- ============================================
-- 5. 管理员表 (admins) 索引优化（可选）
-- ============================================

-- 5.1 复合索引：软删除+创建时间（用于列表查询）
ALTER TABLE admins ADD KEY `idx_deleted_created` (`is_deleted`, `created_at` DESC);

-- ============================================
-- 索引优化完成
-- ============================================
-- 说明：
-- 1. 执行后可以使用 EXPLAIN 查看查询计划，确认索引是否生效
-- 2. 建议定期执行 ANALYZE TABLE 更新索引统计信息
-- 3. 监控慢查询日志，根据实际情况调整索引
-- ============================================

-- ============================================
-- 验证索引是否创建成功
-- ============================================
-- 执行以下SQL查看索引：
-- SHOW INDEX FROM members;
-- SHOW INDEX FROM products;
-- SHOW INDEX FROM points_records;
-- SHOW INDEX FROM staffs;
-- SHOW INDEX FROM admins;

-- ============================================
-- 测试查询性能（使用 EXPLAIN 查看执行计划）
-- ============================================

-- 测试1：商品列表查询
-- EXPLAIN SELECT * FROM products 
-- WHERE type = 1 AND status = 1 AND is_deleted = 0 
-- ORDER BY sort_order DESC, id DESC LIMIT 20;

-- 测试2：会员积分记录查询
-- EXPLAIN SELECT * FROM points_records 
-- WHERE member_id = 1 
-- ORDER BY created_at DESC LIMIT 20;

-- 测试3：店员查询自己的操作
-- EXPLAIN SELECT * FROM points_records 
-- WHERE operator_type = 'staff' AND operator_id = 1 
-- ORDER BY created_at DESC LIMIT 20;

-- 测试4：数据大屏统计查询
-- EXPLAIN SELECT DATE(created_at) as date, 
--        SUM(CASE WHEN points > 0 THEN points ELSE 0 END) as issued
-- FROM points_records 
-- WHERE created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY)
-- GROUP BY DATE(created_at);
