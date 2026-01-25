# 实体类和Mapper规划文档

## 📋 概述

本文档说明已创建的实体类（Entity）和Mapper接口，以及它们的使用方式。

---

## 🗂️ 实体类（Entity）

### 1. Admin（管理员）

**位置：** `backend/huakang-mapper/src/main/java/com/huakang/mapper/entity/Admin.java`

**对应表：** `admins`

**主要字段：**
- `id` - 主键ID
- `username` - 登录账号（唯一）
- `passwordHash` - 密码哈希值（BCrypt）
- `name` - 管理员姓名
- `email` - 邮箱
- `phone` - 手机号
- `role` - 角色（预留）
- `lastLoginAt` - 最后登录时间
- `lastLoginIp` - 最后登录IP
- `loginFailCount` - 连续登录失败次数
- `lockedUntil` - 账号锁定到期时间
- `isDeleted` - 软删除标记（0=否，1=是）
- `createdAt` - 创建时间（自动填充）
- `updatedAt` - 更新时间（自动填充）

**特性：**
- ✅ 使用 `@TableLogic` 实现软删除
- ✅ 自动填充创建时间和更新时间

---

### 2. Staff（店员）

**位置：** `backend/huakang-mapper/src/main/java/com/huakang/mapper/entity/Staff.java`

**对应表：** `staffs`

**主要字段：**
- `id` - 主键ID
- `username` - 登录账号（唯一）
- `passwordHash` - 密码哈希值（BCrypt）
- `name` - 店员姓名
- `email` - 邮箱
- `phone` - 手机号
- `department` - 部门（预留）
- `status` - 状态（1=启用，0=禁用）
- `lastLoginAt` - 最后登录时间
- `lastLoginIp` - 最后登录IP
- `loginFailCount` - 连续登录失败次数
- `lockedUntil` - 账号锁定到期时间
- `isDeleted` - 软删除标记
- `createdAt` - 创建时间
- `updatedAt` - 更新时间

**特性：**
- ✅ 使用 `@TableLogic` 实现软删除
- ✅ 自动填充创建时间和更新时间

---

### 3. SystemConfig（系统配置）

**位置：** `backend/huakang-mapper/src/main/java/com/huakang/mapper/entity/SystemConfig.java`

**对应表：** `system_configs`

**主要字段：**
- `id` - 主键ID
- `configKey` - 配置键（唯一）
- `configValue` - 配置值
- `configType` - 配置类型（string/number/json/boolean）
- `configGroup` - 配置分组
- `description` - 配置说明
- `version` - 配置版本号
- `updatedAt` - 更新时间

**特性：**
- ✅ 无软删除（配置数据不删除）
- ✅ 自动填充更新时间

---

### 4. Member（会员）

**位置：** `backend/huakang-mapper/src/main/java/com/huakang/mapper/entity/Member.java`

**对应表：** `members`

**主要字段：**
- `id` - 主键ID
- `openid` - 微信OpenID（唯一）
- `nickname` - 微信昵称
- `avatar` - 微信头像URL
- `phone` - 手机号
- `totalPoints` - 当前积分总额
- `isDeleted` - 软删除标记
- `createdAt` - 创建时间
- `updatedAt` - 更新时间

**特性：**
- ✅ 使用 `@TableLogic` 实现软删除
- ✅ 自动填充创建时间和更新时间
- ✅ 核心业务表，积分总额需与积分记录表保持一致

---

### 5. Product（商品）

**位置：** `backend/huakang-mapper/src/main/java/com/huakang/mapper/entity/Product.java`

**对应表：** `products`

**主要字段：**
- `id` - 主键ID
- `name` - 商品名称
- `image` - 商品主图URL
- `detailImages` - 商品详情图（JSON数组）
- `category` - 商品分类
- `type` - 商品类型（1=全量商品，2=积分商品）
- `price` - 商品价格（元）
- `pointsPrice` - 积分价格
- `description` - 商品描述
- `status` - 状态（1=上架，0=下架）
- `sortOrder` - 排序权重
- `isDeleted` - 软删除标记
- `createdAt` - 创建时间
- `updatedAt` - 更新时间

**特性：**
- ✅ 使用 `@TableLogic` 实现软删除
- ✅ 自动填充创建时间和更新时间
- ✅ 支持JSON类型字段（detailImages）

**业务规则：**
- `type=1`（全量商品）：`price` 必填，`pointsPrice` 为空
- `type=2`（积分商品）：`pointsPrice` 必填，`price` 可为空

---

### 6. PointsRecord（积分记录）

**位置：** `backend/huakang-mapper/src/main/java/com/huakang/mapper/entity/PointsRecord.java`

**对应表：** `points_records`

**主要字段：**
- `id` - 主键ID
- `memberId` - 会员ID（外键）
- `changeType` - 变动类型（消费赠送/积分兑换/后台调整/新会员注册/积分扣除/积分过期）
- `points` - 变动分值（正数=增加，负数=扣除）
- `balanceBefore` - 变动前积分余额
- `balanceAfter` - 变动后积分余额
- `businessNo` - 业务单号（如订单号、兑换单号）
- `productId` - 关联商品ID（积分兑换时，外键）
- `operatorType` - 操作人类型（admin/staff/system）
- `operatorId` - 操作人ID（管理员或店员ID）
- `operatorName` - 操作人姓名（冗余字段）
- `remark` - 备注/原因
- `ipAddress` - 操作IP地址
- `userAgent` - 用户代理（设备信息）
- `createdAt` - 创建时间

**特性：**
- ✅ 无软删除（积分记录永久保存，用于审计）
- ✅ 自动填充创建时间
- ✅ 核心流水表，记录所有积分变动

---

## 🔌 Mapper接口

所有Mapper接口都继承 `BaseMapper<T>`，MyBatis-Plus会自动提供以下方法：

### 基础CRUD方法

```java
// 插入
int insert(T entity);

// 根据ID删除
int deleteById(Serializable id);

// 根据ID更新
int updateById(T entity);

// 根据ID查询
T selectById(Serializable id);

// 查询所有
List<T> selectList(Wrapper<T> queryWrapper);

// 分页查询
IPage<T> selectPage(IPage<T> page, Wrapper<T> queryWrapper);

// 条件查询
T selectOne(Wrapper<T> queryWrapper);

// 统计数量
Long selectCount(Wrapper<T> queryWrapper);
```

### 已创建的Mapper接口

1. **AdminMapper** - `backend/huakang-mapper/src/main/java/com/huakang/mapper/AdminMapper.java`
2. **StaffMapper** - `backend/huakang-mapper/src/main/java/com/huakang/mapper/StaffMapper.java`
3. **SystemConfigMapper** - `backend/huakang-mapper/src/main/java/com/huakang/mapper/SystemConfigMapper.java`
4. **MemberMapper** - `backend/huakang-mapper/src/main/java/com/huakang/mapper/MemberMapper.java`
5. **ProductMapper** - `backend/huakang-mapper/src/main/java/com/huakang/mapper/ProductMapper.java`
6. **PointsRecordMapper** - `backend/huakang-mapper/src/main/java/com/huakang/mapper/PointsRecordMapper.java`

---

## ⚙️ 配置说明

### 1. MyBatis-Plus配置

**位置：** `backend/huakang-mapper/src/main/java/com/huakang/mapper/config/MybatisPlusConfig.java`

**功能：**
- 配置分页插件（MySQL）
- 支持分页查询

### 2. 自动填充处理器

**位置：** `backend/huakang-mapper/src/main/java/com/huakang/mapper/config/MetaObjectHandler.java`

**功能：**
- 自动填充 `createdAt`（插入时）
- 自动填充 `updatedAt`（插入和更新时）

**使用方式：**
在实体类字段上添加注解：
```java
@TableField(value = "created_at", fill = FieldFill.INSERT)
private LocalDateTime createdAt;

@TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
private LocalDateTime updatedAt;
```

### 3. 软删除配置

**使用方式：**
在实体类字段上添加注解：
```java
@TableLogic
@TableField("is_deleted")
private Integer isDeleted;
```

**效果：**
- 调用 `deleteById()` 时，不会物理删除，而是将 `is_deleted` 设置为 1
- 调用 `selectList()` 时，自动过滤 `is_deleted=1` 的记录

---

## 📝 使用示例

### 示例1：基本CRUD操作

```java
@Autowired
private MemberMapper memberMapper;

// 插入
Member member = new Member();
member.setOpenid("openid123");
member.setNickname("测试用户");
memberMapper.insert(member);

// 根据ID查询
Member member = memberMapper.selectById(1L);

// 更新
member.setNickname("新昵称");
memberMapper.updateById(member);

// 删除（软删除）
memberMapper.deleteById(1L);

// 查询所有（自动过滤已删除的）
List<Member> members = memberMapper.selectList(null);
```

### 示例2：条件查询

```java
@Autowired
private MemberMapper memberMapper;

// 使用LambdaQueryWrapper（推荐）
LambdaQueryWrapper<Member> wrapper = new LambdaQueryWrapper<>();
wrapper.eq(Member::getPhone, "13800138000")
       .like(Member::getNickname, "测试")
       .orderByDesc(Member::getCreatedAt);
List<Member> members = memberMapper.selectList(wrapper);

// 使用QueryWrapper
QueryWrapper<Member> wrapper = new QueryWrapper<>();
wrapper.eq("phone", "13800138000")
       .like("nickname", "测试")
       .orderByDesc("created_at");
List<Member> members = memberMapper.selectList(wrapper);
```

### 示例3：分页查询

```java
@Autowired
private ProductMapper productMapper;

// 创建分页对象（第1页，每页10条）
IPage<Product> page = new Page<>(1, 10);

// 添加查询条件
LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
wrapper.eq(Product::getType, 1)  // 全量商品
       .eq(Product::getStatus, 1)  // 上架
       .orderByDesc(Product::getSortOrder);

// 执行分页查询
IPage<Product> result = productMapper.selectPage(page, wrapper);

// 获取结果
List<Product> products = result.getRecords();
long total = result.getTotal();
```

### 示例4：统计查询

```java
@Autowired
private PointsRecordMapper pointsRecordMapper;

// 统计某个会员的积分记录数量
LambdaQueryWrapper<PointsRecord> wrapper = new LambdaQueryWrapper<>();
wrapper.eq(PointsRecord::getMemberId, 1L);
Long count = pointsRecordMapper.selectCount(wrapper);

// 统计某个会员的积分总额（正数）
wrapper.clear();
wrapper.eq(PointsRecord::getMemberId, 1L)
       .gt(PointsRecord::getPoints, 0);
List<PointsRecord> records = pointsRecordMapper.selectList(wrapper);
int totalPoints = records.stream()
    .mapToInt(PointsRecord::getPoints)
    .sum();
```

---

## 🔍 常用查询场景

### 1. 根据OpenID查询会员

```java
LambdaQueryWrapper<Member> wrapper = new LambdaQueryWrapper<>();
wrapper.eq(Member::getOpenid, openid);
Member member = memberMapper.selectOne(wrapper);
```

### 2. 查询上架的商品（按类型）

```java
LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
wrapper.eq(Product::getType, type)  // 1=全量商品，2=积分商品
       .eq(Product::getStatus, 1)   // 上架
       .orderByDesc(Product::getSortOrder)
       .orderByDesc(Product::getCreatedAt);
List<Product> products = productMapper.selectList(wrapper);
```

### 3. 查询会员的积分记录（按时间倒序）

```java
LambdaQueryWrapper<PointsRecord> wrapper = new LambdaQueryWrapper<>();
wrapper.eq(PointsRecord::getMemberId, memberId)
       .orderByDesc(PointsRecord::getCreatedAt);
List<PointsRecord> records = pointsRecordMapper.selectList(wrapper);
```

### 4. 查询系统配置

```java
LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
wrapper.eq(SystemConfig::getConfigKey, "points_register_gift");
SystemConfig config = systemConfigMapper.selectOne(wrapper);
```

---

## ⚠️ 注意事项

1. **软删除**
   - 使用 `@TableLogic` 的实体类，删除操作是软删除
   - 查询时自动过滤已删除的记录
   - 如需查询已删除的记录，需要手动设置 `is_deleted` 条件

2. **时间字段**
   - `createdAt` 和 `updatedAt` 使用 `LocalDateTime` 类型
   - 自动填充处理器会自动设置时间
   - 不需要手动设置

3. **JSON字段**
   - `Product.detailImages` 是JSON类型
   - 存储时使用JSON字符串
   - 读取时MyBatis-Plus会自动解析

4. **外键关系**
   - `PointsRecord.memberId` 关联 `Member.id`
   - `PointsRecord.productId` 关联 `Product.id`
   - 删除时注意外键约束

5. **积分一致性**
   - `Member.totalPoints` 必须与 `PointsRecord` 的余额保持一致
   - 积分操作必须使用事务
   - 定期使用存储过程 `sp_check_points_consistency` 检查一致性

---

## 📚 相关文档

- [MyBatis-Plus官方文档](https://baomidou.com/)
- [数据库设计文档.md](./数据库设计文档.md)
- [企业级数据库设计说明.md](./企业级数据库设计说明.md)

---

## ✅ 完成清单

- [x] 创建6个实体类（Admin, Staff, SystemConfig, Member, Product, PointsRecord）
- [x] 创建6个Mapper接口
- [x] 配置MyBatis-Plus自动填充处理器
- [x] 配置软删除支持
- [x] 创建规划文档

---

**实体类和Mapper已全部创建完成！** 🎉
