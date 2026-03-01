# 阿里云 OSS 配置指南

## 目录
1. [OSS 服务开通](#oss-服务开通)
2. [创建 Bucket](#创建-bucket)
3. [配置访问权限](#配置访问权限)
4. [获取访问密钥](#获取访问密钥)
5. [配置跨域规则](#配置跨域规则)
6. [项目配置](#项目配置)
7. [测试验证](#测试验证)

---

## OSS 服务开通

### 1. 登录阿里云控制台

访问：https://www.aliyun.com/
- 登录阿里云账号
- 如果没有账号，先注册并完成实名认证

### 2. 开通 OSS 服务

1. 进入 OSS 产品页：https://www.aliyun.com/product/oss
2. 点击"立即开通"
3. 选择计费方式：
   - **按量付费**（推荐）：适合流量不确定的场景
   - **资源包**：适合流量稳定的场景

### 3. 费用说明

**按量付费价格参考（华东1-杭州）：**
- 存储费用：0.12 元/GB/月
- 流量费用：0.5 元/GB（外网流出）
- 请求费用：0.01 元/万次（PUT 请求）

**示例：**
- 存储 10GB 图片：1.2 元/月
- 每月 100GB 流量：50 元/月
- 每月 10 万次上传：0.1 元/月

---

## 创建 Bucket

### 1. 进入 OSS 控制台

访问：https://oss.console.aliyun.com/

### 2. 创建 Bucket

点击"创建 Bucket"，填写以下信息：

#### 基本信息
- **Bucket 名称**：`huakang-electronics`（全局唯一，建议使用项目名）
- **地域**：选择离用户最近的地域
  - 华东1（杭州）：`oss-cn-hangzhou`
  - 华东2（上海）：`oss-cn-shanghai`
  - 华北2（北京）：`oss-cn-beijing`
  - 华南1（深圳）：`oss-cn-shenzhen`

#### 存储类型
- **标准存储**（推荐）：适合频繁访问的数据
- 低频访问：适合不经常访问的数据
- 归档存储：适合长期保存的数据

#### 读写权限
- **公共读**（推荐）：允许匿名用户读取文件
- 私有：需要签名才能访问
- 公共读写：不推荐（安全风险）

#### 其他设置
- **版本控制**：关闭（可选）
- **服务端加密**：关闭（可选）
- **实时日志查询**：关闭（可选）

### 3. 记录 Bucket 信息

创建成功后，记录以下信息：
```
Bucket 名称：huakang-electronics
地域节点：oss-cn-hangzhou
Endpoint（外网）：https://oss-cn-hangzhou.aliyuncs.com
Bucket 域名：https://huakang-electronics.oss-cn-hangzhou.aliyuncs.com
```

---

## 配置访问权限

### 1. 设置 Bucket 权限

进入 Bucket 管理页面 → 权限管理 → Bucket 授权策略

**推荐配置：公共读**
- 允许匿名用户读取文件（GET 请求）
- 只有授权用户可以上传文件（PUT 请求）

### 2. 配置防盗链（可选）

进入 Bucket 管理页面 → 数据安全 → 防盗链

**白名单配置：**
```
https://your-domain.com
https://www.your-domain.com
https://admin.your-domain.com
```

**说明：**
- 只允许白名单中的域名访问 OSS 资源
- 防止其他网站盗用你的图片和视频

---

## 获取访问密钥

### 1. 创建 RAM 用户（推荐）

**为什么使用 RAM 用户？**
- 主账号权限过大，不安全
- RAM 用户可以限制权限范围
- 密钥泄露后影响范围小

**创建步骤：**

1. 进入 RAM 控制台：https://ram.console.aliyun.com/
2. 点击"用户" → "创建用户"
3. 填写用户信息：
   - 登录名称：`oss-upload-user`
   - 显示名称：`OSS上传用户`
   - 访问方式：勾选"OpenAPI 调用访问"
4. 点击"确定"，保存 AccessKey ID 和 AccessKey Secret

### 2. 授权 OSS 权限

1. 在用户列表中找到刚创建的用户
2. 点击"添加权限"
3. 选择权限策略：
   - **AliyunOSSFullAccess**（完全权限，推荐开发环境）
   - **AliyunOSSReadOnlyAccess**（只读权限）
   - 自定义策略（推荐生产环境）

### 3. 自定义权限策略（推荐生产环境）

创建自定义策略，只授予必要的权限：

```json
{
  "Version": "1",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "oss:PutObject",
        "oss:GetObject",
        "oss:DeleteObject"
      ],
      "Resource": [
        "acs:oss:*:*:huakang-electronics/*"
      ]
    }
  ]
}
```

**说明：**
- `PutObject`：上传文件
- `GetObject`：读取文件
- `DeleteObject`：删除文件
- 只能操作 `huakang-electronics` 这个 Bucket

### 4. 保存密钥信息

```
AccessKey ID: LTAI5t********************
AccessKey Secret: 3Xj8****************************
```

**⚠️ 安全提示：**
- 不要将密钥提交到 Git 仓库
- 不要在代码中硬编码密钥
- 使用环境变量或配置文件管理密钥

---

## 配置跨域规则

### 1. 为什么需要配置跨域？

前端直传 OSS 时，浏览器会进行跨域检查。如果不配置跨域规则，上传会失败。

### 2. 配置步骤

进入 Bucket 管理页面 → 数据安全 → 跨域设置 → 创建规则

**配置示例：**

| 配置项 | 值 |
|--------|-----|
| 来源 | `*`（允许所有域名，生产环境建议指定具体域名） |
| 允许 Methods | `GET, POST, PUT, DELETE, HEAD` |
| 允许 Headers | `*` |
| 暴露 Headers | `ETag, x-oss-request-id` |
| 缓存时间 | `600` 秒 |

**生产环境配置（更安全）：**

| 配置项 | 值 |
|--------|-----|
| 来源 | `https://your-domain.com` |
| 允许 Methods | `GET, POST, PUT` |
| 允许 Headers | `*` |
| 暴露 Headers | `ETag` |
| 缓存时间 | `600` 秒 |

---

## 项目配置

### 1. 配置文件位置

```
backend/huakang-admin/src/main/resources/application.yml
backend/huakang-miniapp/src/main/resources/application.yml
```

### 2. 开发环境配置

编辑 `application.yml`，在 `dev` 配置段添加：

```yaml
# 阿里云OSS配置
aliyun:
  oss:
    endpoint: https://oss-cn-hangzhou.aliyuncs.com  # OSS地域节点
    access-key-id: LTAI5t********************       # AccessKey ID
    access-key-secret: 3Xj8****************************  # AccessKey Secret
    bucket-name: huakang-electronics                # Bucket名称
    domain: https://huakang-electronics.oss-cn-hangzhou.aliyuncs.com  # 访问域名
    path-prefix: images/                            # 文件路径前缀
```

### 3. 生产环境配置（使用环境变量）

**方式1：在 application.yml 中使用环境变量**

```yaml
# 阿里云OSS配置
aliyun:
  oss:
    endpoint: ${OSS_ENDPOINT}
    access-key-id: ${OSS_ACCESS_KEY_ID}
    access-key-secret: ${OSS_ACCESS_KEY_SECRET}
    bucket-name: ${OSS_BUCKET_NAME}
    domain: ${OSS_DOMAIN}
    path-prefix: ${OSS_PATH_PREFIX:images/}
```

**方式2：创建 .env 文件（推荐）**

在项目根目录创建 `.env` 文件：

```bash
# OSS配置
OSS_ENDPOINT=https://oss-cn-hangzhou.aliyuncs.com
OSS_ACCESS_KEY_ID=LTAI5t********************
OSS_ACCESS_KEY_SECRET=3Xj8****************************
OSS_BUCKET_NAME=huakang-electronics
OSS_DOMAIN=https://huakang-electronics.oss-cn-hangzhou.aliyuncs.com
OSS_PATH_PREFIX=images/
```

**⚠️ 重要：将 .env 添加到 .gitignore**

```bash
echo ".env" >> .gitignore
```

### 4. 配置说明

| 配置项 | 说明 | 示例 |
|--------|------|------|
| `endpoint` | OSS 地域节点（外网访问地址） | `https://oss-cn-hangzhou.aliyuncs.com` |
| `access-key-id` | AccessKey ID | `LTAI5t********************` |
| `access-key-secret` | AccessKey Secret | `3Xj8****************************` |
| `bucket-name` | Bucket 名称 | `huakang-electronics` |
| `domain` | 文件访问域名（可选，CDN 域名或 OSS 域名） | `https://cdn.your-domain.com` |
| `path-prefix` | 文件上传路径前缀 | `images/` 或 `uploads/` |

**domain 配置说明：**
- 如果配置了 `domain`，文件 URL 为：`{domain}/{path-prefix}/{date}/{filename}`
- 如果未配置 `domain`，使用默认 OSS 域名：`https://{bucket-name}.{endpoint}/{path-prefix}/{date}/{filename}`

---

## 测试验证

### 1. 启动项目

```bash
cd backend/huakang-admin
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 2. 访问 Swagger 文档

打开浏览器访问：http://localhost:8080/api/admin/doc.html

### 3. 测试图片上传接口

**接口地址：** `POST /api/admin/oss/upload/media`

**请求参数：**
- `files`：选择一张图片文件
- `folder`：填写 `products`（可选）

**预期结果：**
```json
{
  "code": 200,
  "message": "OK",
  "data": [
    "https://huakang-electronics.oss-cn-hangzhou.aliyuncs.com/images/products/20260215/a1b2c3d4e5f6.jpg"
  ]
}
```

### 4. 验证文件是否上传成功

**方式1：访问返回的 URL**
- 复制返回的 URL
- 在浏览器中打开
- 应该能看到上传的图片

**方式2：在 OSS 控制台查看**
- 进入 OSS 控制台
- 选择对应的 Bucket
- 进入"文件管理"
- 查看 `images/products/20260215/` 目录下是否有文件

### 5. 常见问题排查

#### 问题1：提示"OSS未配置"

**原因：** OSS 配置信息为空或不完整

**解决方案：**
1. 检查 `application.yml` 中的 OSS 配置
2. 确保 `endpoint`、`access-key-id`、`access-key-secret`、`bucket-name` 都已配置
3. 重启项目

#### 问题2：上传失败，提示"InvalidAccessKeyId"

**原因：** AccessKey ID 或 AccessKey Secret 错误

**解决方案：**
1. 检查 AccessKey ID 和 AccessKey Secret 是否正确
2. 确认 RAM 用户是否已授权 OSS 权限
3. 检查密钥是否过期或被禁用

#### 问题3：上传成功，但无法访问文件

**原因：** Bucket 权限设置为"私有"

**解决方案：**
1. 进入 OSS 控制台
2. 选择对应的 Bucket
3. 进入"权限管理" → "读写权限"
4. 修改为"公共读"

#### 问题4：跨域错误

**原因：** 未配置跨域规则

**解决方案：**
1. 进入 OSS 控制台
2. 选择对应的 Bucket
3. 进入"数据安全" → "跨域设置"
4. 添加跨域规则（参考上文）

---

## 下一步

配置完成后，继续阅读：
- [2-服务器环境准备.md](./2-服务器环境准备.md)
- [3-项目部署步骤.md](./3-项目部署步骤.md)
- [4-Nginx配置.md](./4-Nginx配置.md)

---

**文档版本**：v1.0  
**更新日期**：2026-02-15  
**适用项目**：华康电器积分系统
