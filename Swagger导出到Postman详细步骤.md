# Swagger 导出 OpenAPI 到 Postman 详细步骤

## 📋 步骤概览

1. 从 Knife4j/Swagger 导出 OpenAPI JSON
2. 在 Postman 中导入 OpenAPI 规范
3. 配置环境变量（可选）
4. 设置认证（JWT Token）
5. 运行 Collection

---

## 第一步：从 Knife4j 导出 OpenAPI 规范

### 方法一：通过浏览器直接访问（推荐）

1. **启动应用**，确保应用正常运行

2. **访问 OpenAPI JSON 端点**：
   ```
   http://localhost:8080/api/admin/v3/api-docs
   ```

3. **保存 JSON 文件**：
   - 浏览器会显示 JSON 内容
   - 按 `Ctrl+S` 或右键选择"另存为"
   - 保存为 `huakang-admin-openapi.json`

### 方法二：通过 Knife4j 文档页面

1. **访问 Knife4j 文档页面**：
   ```
   http://localhost:8080/api/admin/doc.html
   ```

2. **点击右上角的"下载"按钮**（如果有）
   - 或者直接访问 `/v3/api-docs` 端点

### 方法三：使用 curl 命令（命令行）

```bash
curl http://localhost:8080/api/admin/v3/api-docs -o huakang-admin-openapi.json
```

---

## 第二步：在 Postman 中导入 OpenAPI

### 2.1 打开 Postman

1. 启动 Postman 应用

2. 点击左上角的 **"Import"** 按钮

### 2.2 导入 OpenAPI 文件

1. **选择导入方式**：
   - 点击 **"Upload Files"** 标签
   - 或者直接拖拽 JSON 文件到 Postman 窗口

2. **选择文件**：
   - 浏览并选择刚才保存的 `huakang-admin-openapi.json` 文件

3. **配置导入选项**：
   - ✅ 勾选 **"Generate a Postman Collection"**
   - ✅ 勾选 **"Generate tests"**（可选，自动生成测试）
   - ✅ 勾选 **"Generate documentation"**（可选）

4. **点击 "Import"** 按钮

### 2.3 验证导入结果

导入成功后，你会看到：
- 左侧边栏出现新的 Collection，名称类似 `huakang-admin` 或 `OpenAPI 3.0`
- Collection 中包含所有 API 接口，按模块分组

---

## 第三步：配置环境变量（推荐）

### 3.1 创建环境

1. 点击 Postman 右上角的 **"Environments"** 或齿轮图标
2. 点击 **"+"** 创建新环境
3. 命名为 `华康电器-开发环境` 或 `Huakang Dev`

### 3.2 添加环境变量

添加以下变量：

| 变量名 | 初始值 | 当前值 | 说明 |
|--------|--------|--------|------|
| `base_url` | `http://localhost:8080/api/admin` | `http://localhost:8080/api/admin` | API 基础URL |
| `token` | （留空） | （留空） | JWT Token（登录后自动填充） |
| `username` | `admin` | `admin` | 默认用户名 |
| `password` | `123456` | `123456` | 默认密码 |

### 3.3 选择环境

在右上角的环境下拉菜单中选择刚创建的环境

---

## 第四步：设置认证（JWT Token）

### 4.1 方法一：使用 Collection 级别的认证（推荐）

1. **选择 Collection**（点击左侧的 Collection 名称）

2. 点击 **"Authorization"** 标签

3. **配置认证方式**：
   - Type: 选择 **"Bearer Token"**
   - Token: 输入 `{{token}}`（使用环境变量）

4. **保存**：点击右上角的 **"Save"** 按钮

### 4.2 方法二：使用环境变量自动设置

1. **先登录获取 Token**：
   - 找到 `POST /auth/login` 接口
   - 发送请求获取 Token

2. **在 Tests 标签中添加脚本**（自动保存 Token）：
   ```javascript
   // 登录接口的 Tests 脚本
   if (pm.response.code === 200) {
       var jsonData = pm.response.json();
       if (jsonData.data && jsonData.data.token) {
           pm.environment.set("token", jsonData.data.token);
           console.log("Token已保存:", jsonData.data.token);
       }
   }
   ```

3. **其他接口会自动使用保存的 Token**

---

## 第五步：运行 Collection

### 5.1 手动运行单个请求

1. **选择接口**：在 Collection 中找到要测试的接口
2. **检查参数**：确认请求参数已填写
3. **发送请求**：点击 **"Send"** 按钮

### 5.2 使用 Collection Runner（批量运行）

1. **打开 Collection Runner**：
   - 右键点击 Collection
   - 选择 **"Run collection"**
   - 或点击 Collection 名称，然后点击 **"Run"** 按钮

2. **配置运行选项**：
   - **选择要运行的接口**：勾选需要测试的接口
   - **运行顺序**：可以拖拽调整顺序
   - **迭代次数**：设置运行次数（默认1次）
   - **延迟**：设置请求之间的延迟时间（毫秒）

3. **重要：设置运行顺序**：
   ```
   1. POST /auth/login          （必须先登录）
   2. GET /dashboard/stats      （需要认证）
   3. GET /members/list          （需要认证）
   ... 其他接口
   ```

4. **点击 "Run Huakang Admin"** 按钮

5. **查看运行结果**：
   - 每个请求会显示状态（Pass/Fail）
   - 可以查看响应内容
   - 检查测试结果

---

## 第六步：优化 Collection（可选）

### 6.1 添加 Pre-request Script（自动登录）

在 Collection 的 **"Pre-request Script"** 标签中添加：

```javascript
// 检查 Token 是否存在，如果不存在则自动登录
if (!pm.environment.get("token")) {
    console.log("Token不存在，自动登录...");
    
    pm.sendRequest({
        url: pm.environment.get("base_url") + "/auth/login",
        method: 'POST',
        header: {
            'Content-Type': 'application/json'
        },
        body: {
            mode: 'raw',
            raw: JSON.stringify({
                username: pm.environment.get("username"),
                password: pm.environment.get("password")
            })
        }
    }, function (err, res) {
        if (!err && res.code === 200) {
            var jsonData = res.json();
            if (jsonData.data && jsonData.data.token) {
                pm.environment.set("token", jsonData.data.token);
                console.log("自动登录成功，Token已保存");
            }
        }
    });
}
```

### 6.2 添加测试脚本（验证响应）

在每个接口的 **"Tests"** 标签中添加：

```javascript
// 验证响应状态码
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

// 验证响应格式
pm.test("Response has correct structure", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('code');
    pm.expect(jsonData).to.have.property('message');
    pm.expect(jsonData).to.have.property('data');
});
```

### 6.3 组织接口结构

1. **创建文件夹**：在 Collection 中创建文件夹，按模块分组
   - 📁 认证管理
   - 📁 数据大屏
   - 📁 会员管理
   - 📁 积分记录
   - 📁 店员管理

2. **拖拽接口**：将相关接口拖拽到对应文件夹

---

## 常见问题解决

### 问题1：导入后 URL 不正确

**原因**：OpenAPI 规范中的 servers 配置可能不正确

**解决**：
1. 检查 Collection 的 **"Variables"** 标签
2. 确保 `baseUrl` 变量值为 `http://localhost:8080/api/admin`
3. 或者手动修改每个请求的 URL

### 问题2：401 Unauthorized 错误

**原因**：Token 未设置或已过期

**解决**：
1. 检查 Collection 的 Authorization 配置
2. 重新登录获取新的 Token
3. 更新环境变量中的 `token` 值

### 问题3：请求参数格式错误

**原因**：Postman 可能没有正确解析 OpenAPI 规范

**解决**：
1. 手动检查请求体格式
2. 确保 Content-Type 为 `application/json`
3. 检查 JSON 格式是否正确

### 问题4：中文乱码

**解决**：
1. 确保请求头包含：`Content-Type: application/json; charset=UTF-8`
2. 在 Postman Settings 中设置编码为 UTF-8

---

## 快速测试流程

### 1. 登录获取 Token
```
POST {{base_url}}/auth/login
Body:
{
  "username": "admin",
  "password": "123456"
}
```

### 2. 测试数据大屏
```
GET {{base_url}}/dashboard/stats
```

### 3. 测试会员列表
```
GET {{base_url}}/members/list?page=1&size=10
```

### 4. 测试积分记录
```
GET {{base_url}}/points-records/list?page=1&size=10
```

---

## 最佳实践

1. **使用环境变量**：不要硬编码 URL 和 Token
2. **先登录后测试**：确保在 Collection Runner 中先运行登录接口
3. **添加测试脚本**：验证响应格式和状态码
4. **组织 Collection**：按模块分组，便于管理
5. **保存示例**：保存成功的请求作为示例
6. **使用变量**：在请求中使用 `{{variable}}` 引用环境变量

---

## 导出 Collection（备份）

1. 右键点击 Collection
2. 选择 **"Export"**
3. 选择 **"Collection v2.1"** 格式
4. 保存为 JSON 文件

这样可以在其他 Postman 实例中导入使用。
