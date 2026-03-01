# MySQL 索引使用教学文档

## 目录
1. [索引基础概念](#索引基础概念)
2. [索引类型详解](#索引类型详解)
3. [本项目索引设计](#本项目索引设计)
4. [索引创建与管理](#索引创建与管理)
5. [索引优化实战](#索引优化实战)
6. [常见问题与解决方案](#常见问题与解决方案)

---

## 索引基础概念

### 什么是索引？

索引是数据库中用于快速查找数据的数据结构，类似于书籍的目录。通过索引，数据库可以快速定位到需要的数据行，而不需要扫描整个表。

### 为什么需要索引？

**没有索引的查询（全表扫描）：**
```sql
-- 假设 members 表有 100 万条数据
SELECT * FROM members WHERE phone = '13800138000';
-- 需要扫描 100 万行数据，耗时可能达到几秒
```

**有索引的查询：**
```sql
-- 在 phone 字段上创建索引后
SELECT * FROM members WHERE phone = '13800138000';
-- 通过索引直接定位，耗时可能只需要几毫秒
```

### 索引的优缺点

**优点：**
- 大幅提高查询速度（特别是 WHERE、JOIN、ORDER BY、GROUP BY）
- 加速数据检索，减少磁盘 I/O
- 通过唯一索引保证数据唯一性

**缺点：**
- 占用额外的存储空间
- 降低写操作（INSERT、UPDATE、DELETE）的速度
- 需要维护成本

---

## 索引类型详解

### 1. 主键索引（PRIMARY KEY）

**特点：**
- 唯一且不能为 NULL
- 每个表只能有一个主键
- 自动创建聚簇索引（InnoDB）

**示例：**
```sql
CREATE TABLE members (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);
```

### 2. 唯一索引（UNIQUE）

**特点：**
- 保证列值唯一
- 允许 NULL 值（但只能有一个 NULL）
- 可以有多个唯一索引

**示例：**
```sql
-- 会员表的手机号唯一索引
CREATE UNIQUE INDEX idx_phone ON members(phone);

-- 或在建表时定义
CREATE TABLE members (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    phone VARCHAR(20) NOT NULL UNIQUE
);
```

**本项目应用：**
```sql
-- 会员手机号唯一索引
ALTER TABLE members ADD UNIQUE INDEX idx_phone (phone);

-- 店员账号唯一索引
ALTER TABLE staff ADD UNIQUE INDEX idx_username (username);
```

### 3. 普通索引（INDEX）

**特点：**
- 最基本的索引类型
- 允许重复值和 NULL 值
- 用于加速查询

**示例：**
```sql
-- 为会员昵称创建索引
CREATE INDEX idx_nickname ON members(nickname);
```

**本项目应用：**
```sql
-- 会员昵称索引（用于模糊搜索）
ALTER TABLE members ADD INDEX idx_nickname (nickname);

-- 商品名称索引（用于商品搜索）
ALTER TABLE products ADD INDEX idx_name (name);
```

### 4. 复合索引（组合索引）

**特点：**
- 由多个列组成
- 遵循最左前缀原则
- 可以覆盖多种查询场景

**最左前缀原则：**
```sql
-- 创建复合索引
CREATE INDEX idx_status_created ON members(status, created_at);

-- 以下查询可以使用索引：
SELECT * FROM members WHERE status = 1;  -- ✓ 使用索引
SELECT * FROM members WHERE status = 1 AND created_at > '2025-01-01';  -- ✓ 使用索引
SELECT * FROM members WHERE status = 1 ORDER BY created_at;  -- ✓ 使用索引

-- 以下查询无法使用索引：
SELECT * FROM members WHERE created_at > '2025-01-01';  -- ✗ 不使用索引（跳过了 status）
```

**本项目应用：**
```sql
-- 积分记录的复合索引（会员ID + 创建时间）
ALTER TABLE points_records ADD INDEX idx_member_created (member_id, created_at);

-- 用途：查询某个会员的积分记录，并按时间排序
SELECT * FROM points_records 
WHERE member_id = 1 
ORDER BY created_at DESC;
```

### 5. 全文索引（FULLTEXT）

**特点：**
- 用于全文搜索
- 支持中文分词（MySQL 5.7.6+）
- 适合长文本搜索

**示例：**
```sql
-- 为商品描述创建全文索引
CREATE FULLTEXT INDEX idx_description ON products(description);

-- 使用全文搜索
SELECT * FROM products 
WHERE MATCH(description) AGAINST('洗衣机 节能' IN NATURAL LANGUAGE MODE);
```

---

## 本项目索引设计

### 会员表（members）索引

```sql
-- 1. 主键索引（自动创建）
PRIMARY KEY (id)

-- 2. 手机号唯一索引（用于登录和防止重复注册）
UNIQUE INDEX idx_phone (phone)

-- 3. 昵称索引（用于模糊搜索会员）
INDEX idx_nickname (nickname)

-- 4. 状态索引（用于筛选正常/禁用会员）
INDEX idx_status (status)

-- 5. 创建时间索引（用于按注册时间排序）
INDEX idx_created_at (created_at)

-- 6. 复合索引（状态 + 创建时间，用于筛选并排序）
INDEX idx_status_created (status, created_at)
```

**查询场景示例：**
```sql
-- 场景1：手机号登录（使用 idx_phone）
SELECT * FROM members WHERE phone = '13800138000';

-- 场景2：搜索会员（使用 idx_nickname）
SELECT * FROM members WHERE nickname LIKE '%张三%';

-- 场景3：查询正常会员列表（使用 idx_status_created）
SELECT * FROM members 
WHERE status = 1 
ORDER BY created_at DESC 
LIMIT 10;

-- 场景4：统计某时间段注册会员数（使用 idx_created_at）
SELECT COUNT(*) FROM members 
WHERE created_at BETWEEN '2025-01-01' AND '2025-01-31';
```

### 积分记录表（points_records）索引

```sql
-- 1. 主键索引
PRIMARY KEY (id)

-- 2. 会员ID索引（用于查询某个会员的积分记录）
INDEX idx_member_id (member_id)

-- 3. 复合索引（会员ID + 创建时间）
INDEX idx_member_created (member_id, created_at)

-- 4. 变动类型索引（用于统计不同类型的积分变动）
INDEX idx_change_type (change_type)

-- 5. 创建时间索引（用于按时间范围查询）
INDEX idx_created_at (created_at)

-- 6. 商品ID索引（用于查询与商品相关的积分记录）
INDEX idx_product_id (product_id)
```

**查询场景示例：**
```sql
-- 场景1：查询某个会员的积分记录（使用 idx_member_created）
SELECT * FROM points_records 
WHERE member_id = 1 
ORDER BY created_at DESC 
LIMIT 20;

-- 场景2：统计某时间段的积分变动（使用 idx_created_at）
SELECT change_type, SUM(points) 
FROM points_records 
WHERE created_at BETWEEN '2025-01-01' AND '2025-01-31'
GROUP BY change_type;

-- 场景3：查询某商品相关的积分记录（使用 idx_product_id）
SELECT * FROM points_records 
WHERE product_id = 100;
```

### 商品表（products）索引

```sql
-- 1. 主键索引
PRIMARY KEY (id)

-- 2. 商品名称索引（用于搜索商品）
INDEX idx_name (name)

-- 3. 商品分类索引（用于按分类筛选）
INDEX idx_category (category)

-- 4. 商品类型索引（用于区分全量商品和积分商品）
INDEX idx_type (type)

-- 5. 状态索引（用于筛选上架/下架商品）
INDEX idx_status (status)

-- 6. 复合索引（类型 + 状态 + 排序权重）
INDEX idx_type_status_sort (type, status, sort_order)

-- 7. 创建时间索引
INDEX idx_created_at (created_at)
```

**查询场景示例：**
```sql
-- 场景1：搜索商品（使用 idx_name）
SELECT * FROM products WHERE name LIKE '%洗衣机%';

-- 场景2：查询上架的积分商品并按权重排序（使用 idx_type_status_sort）
SELECT * FROM products 
WHERE type = 2 AND status = 1 
ORDER BY sort_order DESC, created_at DESC;

-- 场景3：按分类筛选商品（使用 idx_category）
SELECT * FROM products WHERE category = '家用电器';
```

### 店员表（staff）索引

```sql
-- 1. 主键索引
PRIMARY KEY (id)

-- 2. 用户名唯一索引（用于登录）
UNIQUE INDEX idx_username (username)

-- 3. 姓名索引（用于搜索店员）
INDEX idx_name (name)

-- 4. 状态索引（用于筛选正常/禁用店员）
INDEX idx_status (status)
```

---

## 索引创建与管理

### 创建索引的方式

#### 方式1：建表时创建
```sql
CREATE TABLE members (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    phone VARCHAR(20) NOT NULL,
    nickname VARCHAR(100),
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL,
    
    -- 创建索引
    UNIQUE INDEX idx_phone (phone),
    INDEX idx_nickname (nickname),
    INDEX idx_status_created (status, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### 方式2：使用 CREATE INDEX
```sql
-- 创建普通索引
CREATE INDEX idx_nickname ON members(nickname);

-- 创建唯一索引
CREATE UNIQUE INDEX idx_phone ON members(phone);

-- 创建复合索引
CREATE INDEX idx_status_created ON members(status, created_at);
```

#### 方式3：使用 ALTER TABLE
```sql
-- 添加普通索引
ALTER TABLE members ADD INDEX idx_nickname (nickname);

-- 添加唯一索引
ALTER TABLE members ADD UNIQUE INDEX idx_phone (phone);

-- 添加复合索引
ALTER TABLE members ADD INDEX idx_status_created (status, created_at);
```

### 查看索引

```sql
-- 查看表的所有索引
SHOW INDEX FROM members;

-- 查看表结构（包括索引）
SHOW CREATE TABLE members;

-- 查看索引使用情况
EXPLAIN SELECT * FROM members WHERE phone = '13800138000';
```

### 删除索引

```sql
-- 方式1：使用 DROP INDEX
DROP INDEX idx_nickname ON members;

-- 方式2：使用 ALTER TABLE
ALTER TABLE members DROP INDEX idx_nickname;

-- 删除主键索引
ALTER TABLE members DROP PRIMARY KEY;
```

### 重建索引

```sql
-- 重建表的所有索引（优化索引碎片）
ALTER TABLE members ENGINE=InnoDB;

-- 或使用 OPTIMIZE TABLE
OPTIMIZE TABLE members;
```

---

## 索引优化实战

### 1. 使用 EXPLAIN 分析查询

```sql
EXPLAIN SELECT * FROM members WHERE phone = '13800138000';
```

**关键字段解释：**
- `type`：连接类型，性能从好到坏：system > const > eq_ref > ref > range > index > ALL
  - `const`：通过主键或唯一索引查询单行
  - `ref`：通过普通索引查询
  - `range`：范围查询
  - `ALL`：全表扫描（最差）

- `key`：实际使用的索引名称
- `rows`：扫描的行数（越少越好）
- `Extra`：额外信息
  - `Using index`：覆盖索引（最优）
  - `Using where`：使用 WHERE 过滤
  - `Using filesort`：需要额外排序（较差）
  - `Using temporary`：使用临时表（最差）

### 2. 索引优化案例

#### 案例1：优化会员列表查询

**优化前：**
```sql
-- 没有合适的索引，全表扫描
SELECT * FROM members 
WHERE status = 1 
ORDER BY created_at DESC 
LIMIT 10;

-- EXPLAIN 结果：
-- type: ALL
-- rows: 100000
-- Extra: Using where; Using filesort
```

**优化后：**
```sql
-- 创建复合索引
ALTER TABLE members ADD INDEX idx_status_created (status, created_at);

-- 再次查询
SELECT * FROM members 
WHERE status = 1 
ORDER BY created_at DESC 
LIMIT 10;

-- EXPLAIN 结果：
-- type: ref
-- key: idx_status_created
-- rows: 10
-- Extra: Using index condition
```

#### 案例2：优化积分记录查询

**优化前：**
```sql
-- 查询某个会员的积分记录
SELECT * FROM points_records 
WHERE member_id = 1 
ORDER BY created_at DESC;

-- 如果只有 member_id 索引，排序需要额外操作
-- Extra: Using filesort
```

**优化后：**
```sql
-- 创建复合索引（member_id + created_at）
ALTER TABLE points_records ADD INDEX idx_member_created (member_id, created_at);

-- 再次查询
SELECT * FROM points_records 
WHERE member_id = 1 
ORDER BY created_at DESC;

-- Extra: Using index condition（不需要额外排序）
```

#### 案例3：优化商品搜索

**优化前：**
```sql
-- 模糊搜索商品名称
SELECT * FROM products WHERE name LIKE '%洗衣机%';

-- 即使有索引，前缀通配符也无法使用索引
-- type: ALL（全表扫描）
```

**优化方案：**
```sql
-- 方案1：使用全文索引
ALTER TABLE products ADD FULLTEXT INDEX idx_name_fulltext (name);

SELECT * FROM products 
WHERE MATCH(name) AGAINST('洗衣机' IN NATURAL LANGUAGE MODE);

-- 方案2：如果只需要前缀匹配，使用右侧通配符
SELECT * FROM products WHERE name LIKE '洗衣机%';
-- 可以使用索引
```

### 3. 索引失效场景

#### 场景1：使用函数或表达式
```sql
-- ✗ 索引失效
SELECT * FROM members WHERE YEAR(created_at) = 2025;

-- ✓ 使用索引
SELECT * FROM members 
WHERE created_at BETWEEN '2025-01-01' AND '2025-12-31 23:59:59';
```

#### 场景2：隐式类型转换
```sql
-- phone 字段是 VARCHAR 类型

-- ✗ 索引失效（数字会导致类型转换）
SELECT * FROM members WHERE phone = 13800138000;

-- ✓ 使用索引
SELECT * FROM members WHERE phone = '13800138000';
```

#### 场景3：OR 条件
```sql
-- ✗ 可能索引失效
SELECT * FROM members WHERE phone = '13800138000' OR nickname = '张三';

-- ✓ 使用 UNION
SELECT * FROM members WHERE phone = '13800138000'
UNION
SELECT * FROM members WHERE nickname = '张三';
```

#### 场景4：NOT、!=、<> 操作符
```sql
-- ✗ 索引失效
SELECT * FROM members WHERE status != 0;

-- ✓ 使用 IN 或其他条件
SELECT * FROM members WHERE status IN (1, 2);
```

#### 场景5：前缀通配符
```sql
-- ✗ 索引失效
SELECT * FROM members WHERE nickname LIKE '%张三%';

-- ✓ 使用索引（如果可以改为前缀匹配）
SELECT * FROM members WHERE nickname LIKE '张三%';
```

### 4. 覆盖索引优化

**什么是覆盖索引？**
查询的所有字段都在索引中，不需要回表查询。

**示例：**
```sql
-- 创建复合索引
CREATE INDEX idx_status_created ON members(status, created_at);

-- 覆盖索引查询（只查询索引中的字段）
SELECT status, created_at FROM members WHERE status = 1;
-- Extra: Using index（最优）

-- 非覆盖索引查询（需要回表）
SELECT * FROM members WHERE status = 1;
-- Extra: Using index condition
```

---

## 常见问题与解决方案

### Q1: 索引越多越好吗？

**答：不是。**

**原因：**
- 每个索引都占用存储空间
- 写操作（INSERT、UPDATE、DELETE）需要维护所有索引，降低性能
- 过多的索引会增加查询优化器的选择成本

**建议：**
- 只为高频查询字段创建索引
- 定期检查并删除未使用的索引
- 一般一个表的索引数量控制在 5-10 个以内

### Q2: 什么时候应该创建索引？

**应该创建索引的场景：**
- WHERE 子句中频繁使用的字段
- JOIN 连接字段
- ORDER BY 和 GROUP BY 字段
- 需要保证唯一性的字段

**不应该创建索引的场景：**
- 数据量很小的表（几百行）
- 频繁更新的字段
- 区分度很低的字段（如性别：只有男/女）
- 很少使用的字段

### Q3: 如何判断索引是否有效？

```sql
-- 1. 使用 EXPLAIN 分析查询
EXPLAIN SELECT * FROM members WHERE phone = '13800138000';

-- 2. 查看索引使用统计
SELECT * FROM sys.schema_unused_indexes WHERE object_schema = 'your_database';

-- 3. 开启慢查询日志
SET GLOBAL slow_query_log = 'ON';
SET GLOBAL long_query_time = 1;  -- 记录超过1秒的查询
```

### Q4: 复合索引的字段顺序如何确定？

**原则：**
1. 区分度高的字段放在前面
2. 等值查询的字段放在前面
3. 范围查询的字段放在后面

**示例：**
```sql
-- 查询条件：WHERE status = 1 AND created_at > '2025-01-01'
-- status 是等值查询，created_at 是范围查询
-- 所以索引应该是：
CREATE INDEX idx_status_created ON members(status, created_at);
```

### Q5: 如何优化大表的索引创建？

```sql
-- 方式1：在线添加索引（MySQL 5.6+）
ALTER TABLE members ADD INDEX idx_nickname (nickname), ALGORITHM=INPLACE, LOCK=NONE;

-- 方式2：使用 pt-online-schema-change（Percona Toolkit）
pt-online-schema-change --alter "ADD INDEX idx_nickname (nickname)" \
  D=your_database,t=members --execute

-- 方式3：在从库创建索引后切换主从
```

### Q6: 本项目如何执行索引优化脚本？

**步骤：**

1. **备份数据库**
```bash
mysqldump -u root -p huakang_db > backup_$(date +%Y%m%d).sql
```

2. **执行索引脚本**
```bash
mysql -u root -p huakang_db < index_optimization_enhanced.sql
```

3. **验证索引创建**
```sql
-- 检查会员表索引
SHOW INDEX FROM members;

-- 检查积分记录表索引
SHOW INDEX FROM points_records;

-- 检查商品表索引
SHOW INDEX FROM products;
```

4. **测试查询性能**
```sql
-- 测试会员查询
EXPLAIN SELECT * FROM members WHERE phone = '13800138000';

-- 测试积分记录查询
EXPLAIN SELECT * FROM points_records 
WHERE member_id = 1 
ORDER BY created_at DESC;

-- 测试商品查询
EXPLAIN SELECT * FROM products 
WHERE type = 2 AND status = 1 
ORDER BY sort_order DESC;
```

---

## 本项目索引脚本说明

### 脚本位置
```
/d:/电器项目-后端/index_optimization_enhanced.sql
```

### 脚本内容概览

```sql
-- 1. 会员表索引
ALTER TABLE members ADD UNIQUE INDEX idx_phone (phone);
ALTER TABLE members ADD INDEX idx_nickname (nickname);
ALTER TABLE members ADD INDEX idx_status (status);
ALTER TABLE members ADD INDEX idx_created_at (created_at);
ALTER TABLE members ADD INDEX idx_status_created (status, created_at);

-- 2. 积分记录表索引
ALTER TABLE points_records ADD INDEX idx_member_id (member_id);
ALTER TABLE points_records ADD INDEX idx_member_created (member_id, created_at);
ALTER TABLE points_records ADD INDEX idx_change_type (change_type);
ALTER TABLE points_records ADD INDEX idx_created_at (created_at);
ALTER TABLE points_records ADD INDEX idx_product_id (product_id);

-- 3. 商品表索引
ALTER TABLE products ADD INDEX idx_name (name);
ALTER TABLE products ADD INDEX idx_category (category);
ALTER TABLE products ADD INDEX idx_type (type);
ALTER TABLE products ADD INDEX idx_status (status);
ALTER TABLE products ADD INDEX idx_type_status_sort (type, status, sort_order);
ALTER TABLE products ADD INDEX idx_created_at (created_at);

-- 4. 店员表索引
ALTER TABLE staff ADD UNIQUE INDEX idx_username (username);
ALTER TABLE staff ADD INDEX idx_name (name);
ALTER TABLE staff ADD INDEX idx_status (status);
```

### 脚本特点

1. **兼容性**：兼容 MySQL 5.7 和 8.0
2. **安全性**：使用 `IF NOT EXISTS` 避免重复创建
3. **错误处理**：忽略已存在的索引错误
4. **性能优化**：针对高频查询场景设计

---

## 总结

### 索引设计原则

1. **选择性原则**：为高频查询字段创建索引
2. **最左前缀原则**：复合索引遵循最左匹配
3. **覆盖索引原则**：尽量使用覆盖索引减少回表
4. **适度原则**：索引不是越多越好
5. **监控原则**：定期检查索引使用情况

### 性能优化建议

1. 使用 `EXPLAIN` 分析查询计划
2. 避免索引失效的场景
3. 合理使用复合索引
4. 定期清理未使用的索引
5. 监控慢查询日志

### 学习资源

- MySQL 官方文档：https://dev.mysql.com/doc/refman/8.0/en/optimization-indexes.html
- 《高性能 MySQL》第三版
- 使用 `EXPLAIN` 和 `SHOW PROFILE` 分析查询性能

---

**文档版本**：v1.0  
**更新日期**：2026-02-13  
**适用项目**：华康电器积分系统
