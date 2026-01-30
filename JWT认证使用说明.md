# JWT Token 认证使用说明

## 📋 概述

系统使用 JWT (JSON Web Token) 进行身份认证。登录成功后，服务器会返回一个 Token，后续所有需要认证的接口都需要在请求头中携带此 Token。

---

## 🔑 Token 获取

### 1. 登录接口

**接口地址：** `POST /api/admin/auth/login`

**请求示例：**
```json
{
  "username": "admin",
  "password": "your-password",
  "userType": "admin"
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": 1,
    "username": "admin",
    "name": "系统管理员",
    "role": "admin",
    "expiresIn": 7200
  }
}
```

### 2. Token 信息

- **过期时间：** 2小时（7200秒）
- **格式：** JWT标准格式
- **包含信息：** 用户ID、用户名、角色

---

## 📤 使用 Token

### 1. 请求头格式

所有需要认证的接口，必须在请求头中添加：

```
Authorization: Bearer <your-token>
```

**注意：**
- `Bearer` 后面必须有一个空格
- Token 不需要引号包裹
- 大小写敏感

### 2. 请求示例

#### 使用 curl
```bash
curl -X GET "http://localhost:8080/api/admin/system/configs" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

#### 使用 Postman
1. 选择请求方法（GET/POST等）
2. 在 **Headers** 标签页添加：
   - Key: `Authorization`
   - Value: `Bearer <your-token>`

#### 使用 JavaScript (Axios)
```javascript
axios.get('http://localhost:8080/api/admin/system/configs', {
  headers: {
    'Authorization': `Bearer ${token}`
  }
})
```

#### 使用 JavaScript (Fetch)
```javascript
fetch('http://localhost:8080/api/admin/system/configs', {
  headers: {
    'Authorization': `Bearer ${token}`
  }
})
```

---

## ⚠️ 常见问题

### 1. Token 已过期

**错误信息：** `JWT Token已过期或无效`

**原因：** Token 默认有效期为 2 小时，超过时间后需要重新登录。

**解决方案：**
1. 重新调用登录接口获取新 Token
2. 使用新 Token 访问接口

### 2. Token 格式错误

**错误信息：** `JWT Token格式错误` 或 `未登录，请先登录获取Token`

**可能原因：**
- 请求头中缺少 `Authorization`
- `Bearer` 拼写错误或缺少空格
- Token 被截断或不完整

**解决方案：**
- 检查请求头格式：`Authorization: Bearer <token>`
- 确保 `Bearer` 后面有空格
- 确保 Token 完整（通常很长，包含多个 `.` 分隔的部分）

### 3. Token 签名无效

**错误信息：** `JWT Token签名无效`

**原因：** JWT 密钥配置不匹配，或 Token 被篡改。

**解决方案：**
- 重新登录获取新 Token
- 检查服务器 JWT 密钥配置

### 4. 未传递 Token

**错误信息：** `未登录，请先登录获取Token`

**原因：** 请求头中未包含 `Authorization` 字段。

**解决方案：**
- 在请求头中添加 `Authorization: Bearer <token>`

---

## 🔍 调试技巧

### 1. 检查 Token 是否传递

查看服务器日志，如果看到：
```
请求中未包含JWT Token，请求路径: /api/admin/system/configs
```
说明请求头中没有 Token。

### 2. 检查 Token 是否过期

查看服务器日志，如果看到：
```
JWT Token已过期: 过期时间=2026-01-30 16:00:00, 当前时间=2026-01-30 18:00:00
```
说明 Token 已过期，需要重新登录。

### 3. 检查 Token 格式

正确的 Token 格式应该是：
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoiYWRtaW4iLCJyb2xlIjoiYWRtaW4iLCJzdWIiOiJhZG1pbiIsImlhdCI6MTY3NTAwMDAwMCwiZXhwIjoxNjc1MDA3MjAwfQ.signature
```

Token 由三部分组成，用 `.` 分隔：
- Header（头部）
- Payload（载荷）
- Signature（签名）

---

## 📝 最佳实践

### 1. Token 存储

**前端存储建议：**
- 使用 `localStorage` 或 `sessionStorage` 存储 Token
- 不要将 Token 存储在 Cookie 中（除非使用 HttpOnly）

**示例：**
```javascript
// 登录成功后保存 Token
localStorage.setItem('token', response.data.token);

// 请求时获取 Token
const token = localStorage.getItem('token');
```

### 2. Token 刷新

**建议：**
- 在 Token 过期前（如剩余 30 分钟）自动刷新
- 或检测到 401 错误时自动重新登录

**示例：**
```javascript
// 检查 Token 是否即将过期
function isTokenExpiringSoon(token) {
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    const exp = payload.exp * 1000; // 转换为毫秒
    const now = Date.now();
    const timeUntilExpiry = exp - now;
    // 如果剩余时间少于 30 分钟，需要刷新
    return timeUntilExpiry < 30 * 60 * 1000;
  } catch (e) {
    return true; // 解析失败，认为需要刷新
  }
}
```

### 3. 错误处理

**建议：**
- 统一处理 401 未授权错误
- 自动跳转到登录页面

**示例：**
```javascript
axios.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      // Token 无效或过期，清除本地 Token 并跳转登录
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);
```

---

## 🔐 安全注意事项

1. **不要将 Token 暴露在 URL 中**
   - ❌ 错误：`/api/admin/system/configs?token=xxx`
   - ✅ 正确：在请求头中传递

2. **使用 HTTPS 传输**
   - 生产环境必须使用 HTTPS，防止 Token 被窃取

3. **定期更换 JWT 密钥**
   - 生产环境应定期更换 JWT 密钥
   - 更换密钥后，所有已发放的 Token 将失效

4. **Token 过期时间**
   - 默认 2 小时，可根据安全需求调整
   - 敏感操作可要求重新验证

---

## 📊 Token 结构说明

### Payload 内容

```json
{
  "userId": 1,
  "username": "admin",
  "role": "admin",
  "sub": "admin",
  "iat": 1675000000,
  "exp": 1675007200
}
```

- `userId`: 用户ID
- `username`: 用户名
- `role`: 角色（admin/staff）
- `sub`: 主题（通常是用户名）
- `iat`: 签发时间（Unix时间戳）
- `exp`: 过期时间（Unix时间戳）

---

## 🛠️ 测试工具

### 1. 使用 Postman

1. 创建登录请求，获取 Token
2. 在 **Tests** 标签页添加脚本：
```javascript
if (pm.response.code === 200) {
    const jsonData = pm.response.json();
    pm.environment.set("token", jsonData.data.token);
}
```

3. 在其他请求中使用环境变量：
```
Authorization: Bearer {{token}}
```

### 2. 使用 curl 测试

```bash
# 1. 登录获取 Token
TOKEN=$(curl -X POST "http://localhost:8080/api/admin/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"your-password","userType":"admin"}' \
  | jq -r '.data.token')

# 2. 使用 Token 访问接口
curl -X GET "http://localhost:8080/api/admin/system/configs" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 📞 问题反馈

如果遇到 Token 相关问题，请检查：

1. ✅ 请求头格式是否正确：`Authorization: Bearer <token>`
2. ✅ Token 是否完整（没有被截断）
3. ✅ Token 是否过期（登录后 2 小时内有效）
4. ✅ 服务器日志中的详细错误信息

如果问题仍未解决，请提供：
- 服务器日志中的错误信息
- 请求头内容（隐藏 Token 敏感部分）
- Token 的生成时间
