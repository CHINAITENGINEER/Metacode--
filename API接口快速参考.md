# API接口快速参考

## 基础信息

- **基础URL**: `http://localhost:8080/api/admin`
- **API文档**: `http://localhost:8080/api/admin/doc.html`
- **认证方式**: JWT Token（在请求头中添加 `Authorization: Bearer <token>`）

---

## 认证接口

### 管理员登录
```
POST /auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "123456"
}
```

### 店员登录
```
POST /auth/staff-login
Content-Type: application/json

{
  "username": "staff001",
  "password": "123456"
}
```

---

## 模块一：数据大屏

### 获取核心指标统计
```
GET /dashboard/stats
Authorization: Bearer <token>
```

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "totalMembers": 100,
    "totalIssuedPoints": 50000,
    "totalConsumedPoints": 30000,
    "currentPointsPool": 20000
  }
}
```

### 获取趋势数据
```
GET /dashboard/trends
Authorization: Bearer <token>
```

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "date": "2026-01-01",
      "newMembers": 5,
      "issuedPoints": 1000,
      "consumedPoints": 500
    }
  ]
}
```

---

## 模块三：会员管理

### 会员列表查询
```
GET /members/list?page=1&size=10&nickname=张三&phone=13800138000
Authorization: Bearer <token>
```

**查询参数：**
- `page`: 页码（默认1）
- `size`: 每页大小（默认10）
- `nickname`: 昵称（模糊查询，可选）
- `phone`: 手机号（精确查询，可选）

### 会员详情
```
GET /members/{id}
Authorization: Bearer <token>
```

### 创建会员
```
POST /members
Authorization: Bearer <token>
Content-Type: application/json

{
  "nickname": "张三",
  "phone": "13800138000",
  "avatar": "https://...",
  "initialPoints": 100
}
```

### 调整会员积分
```
POST /members/{id}/points/adjust
Authorization: Bearer <token>
Content-Type: application/json

{
  "type": "add",      // add=增加, subtract=扣除, set=直接设置
  "points": 100,
  "remark": "活动奖励"
}
```

---

## 模块四：会员积分记录

### 积分记录列表
```
GET /points-records/list?page=1&size=10&memberId=1&operatorType=staff&startTime=2026-01-01T00:00:00&endTime=2026-01-31T23:59:59
Authorization: Bearer <token>
```

**查询参数：**
- `page`: 页码（默认1）
- `size`: 每页大小（默认10）
- `memberId`: 会员ID（可选）
- `operatorType`: 操作人类型 admin/staff/system（可选）
- `operatorId`: 操作人ID（可选）
- `startTime`: 开始时间（可选，格式：yyyy-MM-ddTHH:mm:ss）
- `endTime`: 结束时间（可选，格式：yyyy-MM-ddTHH:mm:ss）

**权限说明：**
- 管理员：可查看所有记录
- 店员：只能查看自己操作的记录

---

## 模块五：店员账号管理

### 店员列表
```
GET /staffs/list?page=1&size=10
Authorization: Bearer <token>
```

### 店员详情
```
GET /staffs/{id}
Authorization: Bearer <token>
```

### 创建店员账号
```
POST /staffs
Authorization: Bearer <token>
Content-Type: application/json

{
  "username": "staff002",
  "password": "123456",
  "name": "店员B",
  "email": "staff002@example.com",
  "phone": "13800138002",
  "department": "销售部"
}
```

### 重置密码
```
POST /staffs/{id}/reset-password
Authorization: Bearer <token>
Content-Type: application/json

{
  "newPassword": "newpassword123"
}
```

### 启用/禁用账号
```
POST /staffs/{id}/toggle-status?status=1
Authorization: Bearer <token>
```

**参数说明：**
- `status`: 1=启用，0=禁用

---

## 响应格式

### 成功响应
```json
{
  "code": 200,
  "message": "操作成功",
  "data": { ... }
}
```

### 分页响应
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 100,
    "pages": 10,
    "current": 1,
    "size": 10,
    "records": [ ... ]
  }
}
```

### 错误响应
```json
{
  "code": 1000,
  "message": "错误信息",
  "data": null
}
```

---

## 常见错误码

- `200`: 操作成功
- `400`: 参数错误
- `401`: 未授权，请先登录
- `403`: 无权限访问
- `404`: 资源不存在
- `1000`: 业务异常
- `1001`: 登录失败
- `1004`: 账号被锁定

---

## 测试步骤

1. **启动应用**
2. **访问API文档**: `http://localhost:8080/api/admin/doc.html`
3. **登录获取Token**: 使用管理员或店员账号登录
4. **在文档中测试接口**: 点击"Authorize"按钮，输入Token（格式：`Bearer <token>`）
5. **调用接口**: 在文档中直接测试各个接口

---

## 注意事项

1. 所有接口（除登录和文档）都需要在请求头中添加 `Authorization: Bearer <token>`
2. 时间格式使用 ISO 8601 格式：`yyyy-MM-ddTHH:mm:ss`
3. 分页从第1页开始
4. 店员只能查看自己操作的积分记录
5. 积分扣除时会检查积分是否充足
