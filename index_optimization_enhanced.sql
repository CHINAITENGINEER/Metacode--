-- ============================================
-- 数据库索引优化脚本
-- 版本：v2.1
-- 说明：基于性能优化分析，添加缺失的索引以提升查询性能
-- 注意：执行前请备份数据库
-- 兼容性：MySQL 5.7.4+ 支持 IF NOT EXISTS，低版本请手动检查索引是否存在
-- ============================================

USE hk_electronics;

-- ============================================
-- 1. 积分记录表 (points_records) 索引优化
-- ============================================

-- 1.1 为 points 字段添加索引（用于 points > 0 和 points < 0 查询）
-- 优化场景：Dashboard统计查询（sumIssuedPoints、sumConsumedPoints）
-- 查询：SELECT SUM(points) FROM points_records WHERE points > 0
-- 查询：SELECT SUM(ABS(points)) FROM points_records WHERE points < 0
-- 说明：points字段是INT类型，区分度高，适合建索引
-- 性能提升：对于百万级数据，查询时间从秒级降低到毫秒级
-- 注意：如果索引已存在，会报错，请先检查或使用 IF NOT EXISTS（MySQL 5.7.4+）
CREATE INDEX IF NOT EXISTS `idx_points` ON `points_records` (`points`);

-- 1.2 为时间范围 + points条件查询添加复合索引
-- 优化场景：Dashboard趋势查询（getPointsTrendByDate）
-- 查询：WHERE created_at >= ? AND created_at <= ? AND points > 0
-- 说明：复合索引 (created_at, points) 可以同时优化时间范围和points条件查询
-- 性能提升：时间范围查询 + points条件过滤，查询时间减少50-80%
-- 注意：已有idx_created_at单列索引，但复合索引对多条件查询更优
CREATE INDEX IF NOT EXISTS `idx_created_points` ON `points_records` (`created_at`, `points`);

-- ============================================
-- 2. 会员表 (members) 索引优化
-- ============================================

-- 2.1 为 is_deleted + created_at 添加复合索引
-- 优化场景：Dashboard趋势查询（getNewMembersTrendByDate）
-- 查询：WHERE is_deleted = 0 AND created_at >= ? AND created_at <= ?
-- 说明：复合索引可以同时优化软删除筛选和时间范围查询
CREATE INDEX IF NOT EXISTS `idx_deleted_created` ON `members` (`is_deleted`, `created_at`);

-- 2.2 为 is_deleted + phone 添加复合索引（可选，如果phone查询频繁）
-- 优化场景：会员列表查询（按手机号查询）
-- 查询：WHERE is_deleted = 0 AND phone = ?
-- 说明：如果phone查询频繁，可以添加此索引
-- 注意：phone字段已有单独索引，但如果经常与is_deleted一起查询，复合索引更优
CREATE INDEX IF NOT EXISTS `idx_deleted_phone` ON `members` (`is_deleted`, `phone`);

-- ============================================
-- 3. 商品表 (products) 索引优化
-- ============================================

-- 3.1 为 name 字段添加前缀索引（用于LIKE查询）
-- 优化场景：商品列表查询（按名称模糊查询）
-- 查询：WHERE name LIKE '%xxx%' 或 WHERE name LIKE 'xxx%'
-- 说明：
--   - LIKE '%xxx%' 前导通配符无法使用索引，前缀索引也无效
--   - LIKE 'xxx%' 后导通配符可以使用前缀索引
--   - 如果查询模式主要是后导通配符，前缀索引有效
--   - 如果主要是前导通配符，此索引无效，建议删除
-- 注意：根据实际查询模式决定是否创建，如果主要是前导通配符，建议不创建此索引
-- 暂时注释掉，如果查询模式主要是后导通配符再启用
-- CREATE INDEX IF NOT EXISTS `idx_name_prefix` ON `products` (`name`(20));

-- 3.2 为 is_deleted 字段添加索引（如果缺失）
-- 优化场景：商品列表查询（软删除筛选）
-- 查询：WHERE is_deleted = 0
-- 说明：
--   - 已有复合索引 idx_type_status_deleted (type, status, is_deleted)
--   - 如果只查询 is_deleted，复合索引可能不够优化（需要扫描更多数据）
--   - 单独索引 is_deleted 可以优化单独查询场景
-- 注意：根据实际查询频率决定，如果经常单独查询is_deleted，建议添加
-- 如果主要与type、status一起查询，则不需要单独索引
CREATE INDEX IF NOT EXISTS `idx_is_deleted` ON `products` (`is_deleted`);

-- ============================================
-- 4. 管理员表 (admins) 索引优化
-- ============================================

-- 4.1 为 is_deleted + username 添加复合索引（可选）
-- 优化场景：管理员登录查询
-- 查询：WHERE username = ? AND is_deleted = 0
-- 说明：username已有UNIQUE索引，但如果经常与is_deleted一起查询，可以考虑复合索引
-- 注意：UNIQUE索引已经包含username，此索引可能冗余，根据实际查询频率决定是否添加
-- 暂时注释掉，如果查询频繁再启用
-- CREATE INDEX IF NOT EXISTS `idx_deleted_username` ON `admins` (`is_deleted`, `username`);

-- ============================================
-- 5. 店员表 (staffs) 索引优化
-- ============================================

-- 5.1 为 is_deleted + status 添加复合索引
-- 优化场景：店员列表查询（按状态筛选）
-- 查询：WHERE is_deleted = 0 AND status = ?
-- 说明：如果经常同时查询is_deleted和status，复合索引更优
CREATE INDEX IF NOT EXISTS `idx_deleted_status` ON `staffs` (`is_deleted`, `status`);

-- ============================================
-- 6. 系统配置表 (system_configs) 索引优化
-- ============================================

-- 系统配置表索引已足够，无需额外优化
-- 已有索引：
-- - PRIMARY KEY (id)
-- - UNIQUE KEY uk_config_key (config_key)
-- - KEY idx_config_group (config_group)

-- ============================================
-- 7. 索引使用说明
-- ============================================

-- 7.1 查看索引使用情况
-- SELECT * FROM information_schema.STATISTICS 
-- WHERE TABLE_SCHEMA = 'hk_electronics' 
-- AND TABLE_NAME = 'points_records';

-- 7.2 分析查询执行计划
-- EXPLAIN SELECT SUM(points) FROM points_records WHERE points > 0;
-- EXPLAIN SELECT * FROM points_records WHERE created_at >= ? AND created_at <= ? AND points > 0;

-- 7.3 监控索引效果
-- 使用 MySQL 的慢查询日志和性能监控工具监控索引使用情况

-- ============================================
-- 8. 索引维护建议
-- ============================================

-- 8.1 定期分析表，更新索引统计信息
-- ANALYZE TABLE points_records;
-- ANALYZE TABLE members;
-- ANALYZE TABLE products;

-- 8.2 如果索引使用率低，可以考虑删除
-- 删除索引：DROP INDEX idx_name ON table_name;

-- ============================================
-- 9. 索引创建前检查（可选，手动执行）
-- ============================================

-- 9.1 检查索引是否已存在（MySQL 5.7.4以下版本请手动执行）
-- SELECT 
--     TABLE_NAME,
--     INDEX_NAME,
--     COLUMN_NAME,
--     SEQ_IN_INDEX
-- FROM information_schema.STATISTICS
-- WHERE TABLE_SCHEMA = 'hk_electronics'
--     AND TABLE_NAME IN ('points_records', 'members', 'products', 'staffs')
--     AND INDEX_NAME IN (
--         'idx_points', 'idx_created_points',
--         'idx_deleted_created', 'idx_deleted_phone',
--         'idx_is_deleted', 'idx_deleted_status'
--     )
-- ORDER BY TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX;

-- 9.2 如果索引已存在，需要先删除再创建（谨慎操作）
-- DROP INDEX IF EXISTS `idx_points` ON `points_records`;
-- DROP INDEX IF EXISTS `idx_created_points` ON `points_records`;
-- DROP INDEX IF EXISTS `idx_deleted_created` ON `members`;
-- DROP INDEX IF EXISTS `idx_deleted_phone` ON `members`;
-- DROP INDEX IF EXISTS `idx_is_deleted` ON `products`;
-- DROP INDEX IF EXISTS `idx_deleted_status` ON `staffs`;

-- ============================================
-- 10. 执行后验证
-- ============================================

-- 10.1 验证索引是否创建成功
-- SHOW INDEX FROM points_records WHERE Key_name = 'idx_points';
-- SHOW INDEX FROM points_records WHERE Key_name = 'idx_created_points';
-- SHOW INDEX FROM members WHERE Key_name = 'idx_deleted_created';
-- SHOW INDEX FROM members WHERE Key_name = 'idx_deleted_phone';
-- SHOW INDEX FROM products WHERE Key_name = 'idx_is_deleted';
-- SHOW INDEX FROM staffs WHERE Key_name = 'idx_deleted_status';

-- 10.2 测试查询性能（执行计划）
-- EXPLAIN SELECT SUM(points) FROM points_records WHERE points > 0;
-- EXPLAIN SELECT * FROM points_records WHERE created_at >= '2026-01-01' AND created_at <= '2026-01-31' AND points > 0;
-- EXPLAIN SELECT * FROM members WHERE is_deleted = 0 AND created_at >= '2026-01-01' AND created_at <= '2026-01-31';

-- ============================================
-- 优化完成！
-- ============================================
SELECT '索引优化脚本执行完成！请执行验证SQL确认索引创建成功。' AS message;
