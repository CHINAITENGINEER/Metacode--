# JWT认证过滤器修复说明

## 🔍 问题分析

### 问题现象
- 登录成功，获取了Token
- 使用Token访问其他接口时返回 **403 Forbidden**

### 问题原因
Spring Security配置了 `.anyRequest().authenticated()`，要求所有请求都需要认证，但是：
1. ❌ **没有JWT认证过滤器**：Spring Security不知道如何验证JWT Token
2. ❌ **缺少认证上下文**：即使有Token，Spring Security也无法识别用户身份

---

## ✅ 解决方案

### 1. 创建JWT认证过滤器

创建了 `JwtAuthenticationFilter`，功能：
- 从请求头 `Authorization: Bearer <token>` 中提取Token
- 验证Token的有效性（签名、过期时间）
- 从Token中提取用户信息（userId, username, role）
- 创建Spring Security认证对象
- 设置到SecurityContext中

### 2. 添加到Spring Security过滤器链

在 `SecurityConfig` 中添加：
```java
.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
```

**执行顺序**：
```
请求 → JwtAuthenticationFilter → UsernamePasswordAuthenticationFilter → Controller
```

---

## 🔧 实现细节

### JwtAuthenticationFilter 工作流程

```
1. 从请求头提取Token
   ↓
2. 验证Token有效性（签名、过期时间）
   ↓
3. 从Token中提取用户信息
   ↓
4. 创建UsernamePasswordAuthenticationToken
   ↓
5. 设置到SecurityContext
   ↓
6. 继续过滤器链
```

### 错误处理

- **Token无效**：记录警告日志，清除认证上下文，继续执行
- **Token过期**：记录警告日志，清除认证上下文，继续执行
- **无Token**：继续执行，让Spring Security处理（会返回403）

---

## 📋 测试步骤

### 1. 重启应用
```bash
mvn clean compile
# 重启应用
```

### 2. 测试登录
```bash
POST /api/admin/auth/login
{
  "username": "admin",
  "password": "123456"
}
```

### 3. 使用Token访问接口
```bash
GET /api/admin/dashboard/stats
Authorization: Bearer <token>
```

**预期结果**：✅ 返回200，成功获取数据

---

## ⚠️ 注意事项

1. **Token格式**：必须是 `Bearer <token>`，注意Bearer后面有空格
2. **Token过期**：Token默认2小时过期，过期后需要重新登录
3. **角色权限**：JWT过滤器只负责认证，角色权限检查由拦截器处理

---

## 🔐 安全说明

1. **Token验证**：验证签名和过期时间
2. **认证上下文**：只在Token有效时设置，无效时清除
3. **日志记录**：记录认证成功和失败的情况

---

## 🐛 如果仍然403

### 检查清单

1. ✅ Token是否正确（Bearer + 空格 + token）
2. ✅ Token是否过期
3. ✅ 请求头格式是否正确
4. ✅ 应用是否重启
5. ✅ 查看日志是否有JWT验证错误

### 调试方法

在 `JwtAuthenticationFilter` 中添加日志：
```java
log.info("Token: {}", token);
log.info("Token验证结果: {}", jwtUtils.validateToken(token));
```

查看控制台输出，确认Token验证过程。
