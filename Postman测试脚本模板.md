# Postman 测试脚本模板

## 1. Collection 级别的 Pre-request Script（自动登录）

在 Collection 的 **Pre-request Script** 标签中添加：

```javascript
// 自动登录脚本（如果 Token 不存在或已过期）
const token = pm.environment.get("token");
const tokenExpiry = pm.environment.get("token_expiry");

// 检查 Token 是否存在且未过期
const now = new Date().getTime();
if (!token || !tokenExpiry || now > tokenExpiry) {
    console.log("Token不存在或已过期，自动登录...");
    
    const loginRequest = {
        url: pm.environment.get("base_url") + "/auth/login",
        method: 'POST',
        header: {
            'Content-Type': 'application/json'
        },
        body: {
            mode: 'raw',
            raw: JSON.stringify({
                username: pm.environment.get("username") || "admin",
                password: pm.environment.get("password") || "123456"
            })
        }
    };
    
    pm.sendRequest(loginRequest, function (err, res) {
        if (!err && res.code === 200) {
            const jsonData = res.json();
            if (jsonData.data && jsonData.data.token) {
                pm.environment.set("token", jsonData.data.token);
                // Token 有效期2小时，提前5分钟刷新
                const expiryTime = new Date().getTime() + (2 * 60 * 60 * 1000) - (5 * 60 * 1000);
                pm.environment.set("token_expiry", expiryTime);
                console.log("自动登录成功，Token已保存");
            }
        } else {
            console.log("自动登录失败:", err || res.text());
        }
    });
}
```

---

## 2. 登录接口的 Tests 脚本

在 `POST /auth/login` 接口的 **Tests** 标签中添加：

```javascript
// 验证响应状态码
pm.test("登录成功 - 状态码为200", function () {
    pm.response.to.have.status(200);
});

// 验证响应结构
pm.test("响应包含正确的字段", function () {
    const jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('code');
    pm.expect(jsonData).to.have.property('message');
    pm.expect(jsonData).to.have.property('data');
});

// 保存 Token 到环境变量
if (pm.response.code === 200) {
    const jsonData = pm.response.json();
    if (jsonData.data && jsonData.data.token) {
        pm.environment.set("token", jsonData.data.token);
        
        // 计算 Token 过期时间（2小时后）
        const expiryTime = new Date().getTime() + (2 * 60 * 60 * 1000);
        pm.environment.set("token_expiry", expiryTime);
        
        console.log("Token已保存:", jsonData.data.token.substring(0, 20) + "...");
        console.log("用户信息:", jsonData.data.username, jsonData.data.name);
    }
}

// 验证 Token 格式
pm.test("Token格式正确", function () {
    const jsonData = pm.response.json();
    if (jsonData.data && jsonData.data.token) {
        pm.expect(jsonData.data.token).to.be.a('string');
        pm.expect(jsonData.data.token.length).to.be.above(50);
    }
});
```

---

## 3. 通用接口的 Tests 脚本

在需要认证的接口的 **Tests** 标签中添加：

```javascript
// 验证响应状态码
pm.test("状态码为200", function () {
    pm.response.to.have.status(200);
});

// 验证响应结构
pm.test("响应格式正确", function () {
    const jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('code');
    pm.expect(jsonData).to.have.property('message');
    pm.expect(jsonData).to.have.property('data');
});

// 验证业务成功
pm.test("业务操作成功", function () {
    const jsonData = pm.response.json();
    pm.expect(jsonData.code).to.eql(200);
});

// 验证响应时间（可选）
pm.test("响应时间小于1秒", function () {
    pm.expect(pm.response.responseTime).to.be.below(1000);
});
```

---

## 4. 分页接口的 Tests 脚本

在分页查询接口（如 `/members/list`）的 **Tests** 标签中添加：

```javascript
// 验证响应状态码
pm.test("状态码为200", function () {
    pm.response.to.have.status(200);
});

// 验证分页结构
pm.test("分页数据结构正确", function () {
    const jsonData = pm.response.json();
    if (jsonData.data) {
        pm.expect(jsonData.data).to.have.property('total');
        pm.expect(jsonData.data).to.have.property('pages');
        pm.expect(jsonData.data).to.have.property('current');
        pm.expect(jsonData.data).to.have.property('size');
        pm.expect(jsonData.data).to.have.property('records');
        pm.expect(jsonData.data.records).to.be.an('array');
    }
});

// 验证数据不为空（如果有数据）
pm.test("返回数据不为空", function () {
    const jsonData = pm.response.json();
    if (jsonData.data && jsonData.data.total > 0) {
        pm.expect(jsonData.data.records.length).to.be.above(0);
    }
});
```

---

## 5. 数据大屏接口的 Tests 脚本

在 `GET /dashboard/stats` 接口的 **Tests** 标签中添加：

```javascript
// 验证响应状态码
pm.test("状态码为200", function () {
    pm.response.to.have.status(200);
});

// 验证统计数据
pm.test("统计数据格式正确", function () {
    const jsonData = pm.response.json();
    if (jsonData.data) {
        pm.expect(jsonData.data).to.have.property('totalMembers');
        pm.expect(jsonData.data).to.have.property('totalIssuedPoints');
        pm.expect(jsonData.data).to.have.property('totalConsumedPoints');
        pm.expect(jsonData.data).to.have.property('currentPointsPool');
        
        // 验证数据类型
        pm.expect(jsonData.data.totalMembers).to.be.a('number');
        pm.expect(jsonData.data.totalIssuedPoints).to.be.a('number');
        pm.expect(jsonData.data.totalConsumedPoints).to.be.a('number');
        pm.expect(jsonData.data.currentPointsPool).to.be.a('number');
    }
});
```

---

## 6. 创建/更新接口的 Tests 脚本

在创建或更新接口（如 `POST /members`）的 **Tests** 标签中添加：

```javascript
// 验证响应状态码
pm.test("状态码为200", function () {
    pm.response.to.have.status(200);
});

// 验证创建成功
pm.test("创建成功", function () {
    const jsonData = pm.response.json();
    pm.expect(jsonData.code).to.eql(200);
    pm.expect(jsonData.data).to.not.be.null;
});

// 保存创建的ID（用于后续操作）
if (pm.response.code === 200) {
    const jsonData = pm.response.json();
    if (jsonData.data && jsonData.data.id) {
        pm.environment.set("last_created_member_id", jsonData.data.id);
        console.log("创建的会员ID:", jsonData.data.id);
    }
}
```

---

## 7. 环境变量设置脚本

在 Collection 的 **Variables** 标签中设置：

| 变量名 | 类型 | 初始值 | 当前值 |
|--------|------|--------|--------|
| `base_url` | default | `http://localhost:8080/api/admin` | `http://localhost:8080/api/admin` |
| `token` | secret | （留空） | （留空） |
| `token_expiry` | default | （留空） | （留空） |
| `username` | default | `admin` | `admin` |
| `password` | secret | `123456` | `123456` |
| `last_created_member_id` | default | （留空） | （留空） |

---

## 8. Collection Runner 配置示例

### 运行顺序建议：

```
1. POST /auth/login                    （必须先登录）
   ↓
2. GET /dashboard/stats                 （测试数据大屏）
   ↓
3. GET /dashboard/trends                （测试趋势数据）
   ↓
4. GET /members/list                    （测试会员列表）
   ↓
5. POST /members                        （创建测试会员）
   ↓
6. GET /members/{{last_created_member_id}}  （查看刚创建的会员）
   ↓
7. POST /members/{{last_created_member_id}}/points/adjust  （调整积分）
   ↓
8. GET /points-records/list             （查看积分记录）
   ↓
9. GET /staffs/list                     （查看店员列表）
   ↓
10. POST /staffs                        （创建测试店员）
```

### Runner 设置：

- **Iterations**: 1（运行1次）
- **Delay**: 500ms（每个请求之间延迟500毫秒）
- **Data File**: （可选，用于数据驱动测试）
- **Save responses**: ✅ 勾选（保存响应用于调试）

---

## 9. 快速测试 Checklist

- [ ] 已导入 OpenAPI 规范到 Postman
- [ ] 已创建环境并设置变量
- [ ] 已配置 Collection 级别的 Bearer Token 认证
- [ ] 已添加登录接口的 Tests 脚本（自动保存 Token）
- [ ] 已设置 Collection Runner 的运行顺序
- [ ] 已测试登录接口获取 Token
- [ ] 已测试至少一个需要认证的接口
- [ ] 已验证响应格式正确

---

## 10. 调试技巧

### 查看环境变量
```javascript
console.log("当前Token:", pm.environment.get("token"));
console.log("基础URL:", pm.environment.get("base_url"));
```

### 打印响应内容
```javascript
console.log("响应内容:", pm.response.json());
```

### 条件判断
```javascript
if (pm.response.code === 200) {
    console.log("请求成功");
} else {
    console.log("请求失败:", pm.response.code);
}
```

---

## 11. 常见错误处理

### Token 过期处理
```javascript
if (pm.response.code === 401) {
    console.log("Token已过期，请重新登录");
    pm.environment.set("token", "");
}
```

### 业务异常处理
```javascript
const jsonData = pm.response.json();
if (jsonData.code !== 200) {
    console.log("业务异常:", jsonData.message);
}
```
