# Day 3 登录功能实现说明

## ✅ 已完成功能

### 1. JWT工具类
- **文件：** `backend/huakang-service/src/main/java/com/huakang/service/utils/JwtUtils.java`
- **功能：**
  - 生成JWT Token
  - 解析JWT Token
  - 验证Token有效性
  - 获取用户信息（ID、用户名、角色）

### 2. 登录DTO和VO
- **LoginDTO：** `backend/huakang-service/src/main/java/com/huakang/service/dto/auth/LoginDTO.java`
  - 用户名、密码验证
- **LoginVO：** `backend/huakang-service/src/main/java/com/huakang/service/dto/auth/LoginVO.java`
  - Token、用户信息、角色、过期时间

### 3. Service接口和实现
- **AdminService：** 管理员登录服务
  - 接口：`backend/huakang-service/src/main/java/com/huakang/service/service/AdminService.java`
  - 实现：`backend/huakang-service/src/main/java/com/huakang/service/impl/AdminServiceImpl.java`
- **StaffService：** 店员登录服务
  - 接口：`backend/huakang-service/src/main/java/com/huakang/service/service/StaffService.java`
  - 实现：`backend/huakang-service/src/main/java/com/huakang/service/impl/StaffServiceImpl.java`

### 4. Controller
- **AuthController：** `backend/huakang-admin/src/main/java/com/huakang/admin/controller/AuthController.java`
  - 管理员登录接口
  - 店员登录接口

### 5. 登录失败锁定机制
- ✅ 连续失败5次锁定账号
- ✅ 锁定30分钟
- ✅ 显示剩余尝试次数
- ✅ 记录登录IP和时间

---

## 🔑 核心功能说明

### 1. 密码加密（BCrypt）
使用Spring Security的BCryptPasswordEncoder进行密码加密和验证：
```java
BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
// 加密密码
String hashedPassword = passwordEncoder.encode("原始密码");
// 验证密码
boolean matches = passwordEncoder.matches("原始密码", hashedPassword);
```

### 2. JWT Token生成
```java
String token = jwtUtils.generateToken(userId, username, role);
```
Token包含：
- userId：用户ID
- username：用户名
- role：角色（admin/staff）
- issuedAt：签发时间
- expiration：过期时间（2小时）

### 3. 登录失败锁定机制
- **最大失败次数：** 5次
- **锁定时间：** 30分钟
- **失败计数：** 每次失败+1，成功登录后重置
- **锁定检查：** 登录前检查账号是否被锁定

### 4. 登录流程
1. 验证用户名是否存在
2. 检查账号是否被锁定
3. 检查账号状态（店员需检查是否启用）
4. 验证密码
5. 密码错误：增加失败次数，检查是否需要锁定
6. 密码正确：重置失败次数，更新登录信息，生成Token

---

## 📝 API接口

### 1. 管理员登录
```
POST /api/admin/auth/login
Content-Type: application/json

请求体：
{
  "username": "admin",
  "password": "123456"
}

响应：
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": 1,
    "username": "admin",
    "name": "管理员",
    "role": "admin",
    "expiresIn": 7200
  }
}
```

### 2. 店员登录
```
POST /api/admin/auth/staff-login
Content-Type: application/json

请求体：
{
  "username": "staff001",
  "password": "123456"
}

响应：
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": 1,
    "username": "staff001",
    "name": "店员A",
    "role": "staff",
    "expiresIn": 7200
  }
}
```

---

## ⚙️ 配置说明

### JWT配置（application.yml）
```yaml
jwt:
  secret: huakang-electronics-secret-key-2024-please-change-in-production
  expiration: 7200000 # 2小时（毫秒）
```

**⚠️ 重要：** 生产环境必须修改 `jwt.secret` 为强随机字符串！

### 数据库配置
已更新为使用Docker MySQL端口3307：
```yaml
url: jdbc:mysql://localhost:3307/hk_electronics
```

---

## 🧪 测试步骤

### 1. 创建测试账号

在数据库中插入测试数据：

```sql
USE hk_electronics;

-- 创建管理员账号（密码：admin123）
-- BCrypt哈希值：$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
INSERT INTO admins (username, password_hash, name, role) 
VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '管理员', 'admin');

-- 创建店员账号（密码：staff123）
-- BCrypt哈希值：$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
INSERT INTO staffs (username, password_hash, name, status) 
VALUES ('staff001', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '店员A', 1);
```

**注意：** 上述BCrypt哈希值对应密码为 `123456`，如需其他密码，请使用以下Java代码生成：
```java
BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
String hash = encoder.encode("你的密码");
System.out.println(hash);
```

### 2. 启动应用

```bash
cd backend/huakang-admin
mvn spring-boot:run
```

### 3. 测试登录接口

使用Postman或curl测试：

```bash
# 管理员登录
curl -X POST http://localhost:8080/api/admin/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'

# 店员登录
curl -X POST http://localhost:8080/api/admin/auth/staff-login \
  -H "Content-Type: application/json" \
  -d '{"username":"staff001","password":"123456"}'
```

### 4. 测试失败锁定

连续5次输入错误密码，第6次应该返回账号锁定错误。

---

## 📋 错误处理

### 常见错误

1. **用户名或密码错误**
   - 返回：`用户名或密码错误，剩余尝试次数：X`

2. **账号被锁定**
   - 返回：`账号已被锁定，请于 2026-01-25T14:30:00 后重试`

3. **账号被禁用**（仅店员）
   - 返回：`账号已被禁用，请联系管理员`

4. **连续失败过多**
   - 返回：`连续登录失败次数过多，账号已锁定30分钟`

---

## 🔐 安全特性

1. ✅ **密码加密：** 使用BCrypt，不可逆
2. ✅ **JWT Token：** 包含用户信息和角色
3. ✅ **登录失败锁定：** 防止暴力破解
4. ✅ **IP记录：** 记录登录IP，便于审计
5. ✅ **时间记录：** 记录最后登录时间

---

## 📚 相关文件

- JWT工具类：`backend/huakang-service/src/main/java/com/huakang/service/utils/JwtUtils.java`
- 登录DTO：`backend/huakang-service/src/main/java/com/huakang/service/dto/auth/LoginDTO.java`
- 登录VO：`backend/huakang-service/src/main/java/com/huakang/service/dto/auth/LoginVO.java`
- AdminService：`backend/huakang-service/src/main/java/com/huakang/service/service/AdminService.java`
- StaffService：`backend/huakang-service/src/main/java/com/huakang/service/service/StaffService.java`
- AuthController：`backend/huakang-admin/src/main/java/com/huakang/admin/controller/AuthController.java`

---

## ✅ Day 3 任务完成清单

- [x] 创建AdminService、StaffService
- [x] 实现密码加密（BCrypt）
- [x] 实现管理员登录接口
- [x] 实现店员登录接口
- [x] 实现JWT Token生成
- [x] 实现登录失败锁定机制
- [x] 创建JWT工具类

---

**Day 3 登录功能已全部实现！** 🎉
