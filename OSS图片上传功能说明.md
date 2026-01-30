# OSS图片上传功能说明

## 📋 功能概述

已实现阿里云OSS图片上传功能，支持：
- ✅ 商品图片上传
- ✅ 企业微信二维码上传
- ✅ 通用图片上传
- ✅ 图片格式验证（JPG、PNG、GIF、WebP）
- ✅ 文件大小限制（10MB）
- ✅ 自动生成唯一文件名
- ✅ 按日期和文件夹分类存储

---

## 🛠️ 实现内容

### 1. 添加的依赖

**文件：** `backend/huakang-service/pom.xml`

```xml
<!-- 阿里云OSS -->
<dependency>
    <groupId>com.aliyun.oss</groupId>
    <artifactId>aliyun-sdk-oss</artifactId>
    <version>3.17.4</version>
</dependency>
```

### 2. 创建的文件

#### 2.1 OSS配置类
**文件：** `backend/huakang-service/src/main/java/com/huakang/service/config/OssConfig.java`

- 读取OSS配置信息
- 创建OSS客户端Bean
- 支持未配置时返回null（使用本地存储）

#### 2.2 OSS服务接口
**文件：** `backend/huakang-service/src/main/java/com/huakang/service/service/OssService.java`

- `uploadImage()` - 上传图片
- `deleteFile()` - 删除文件

#### 2.3 OSS服务实现
**文件：** `backend/huakang-service/src/main/java/com/huakang/service/impl/OssServiceImpl.java`

**功能特性：**
- 文件格式验证（仅支持图片格式）
- 文件大小验证（最大10MB）
- 自动生成唯一文件名（UUID + 日期）
- 按文件夹分类存储
- 支持CDN域名配置

### 3. 更新的接口

#### 3.1 系统配置 - 上传二维码
**接口：** `POST /api/admin/system/wechat-qrcode`

**变化：**
- 原来：接收URL参数
- 现在：接收MultipartFile文件，自动上传到OSS

**使用示例：**
```bash
curl -X POST "http://localhost:8080/api/admin/system/wechat-qrcode" \
  -H "Authorization: Bearer {token}" \
  -F "file=@qrcode.jpg"
```

#### 3.2 系统配置 - 通用图片上传
**接口：** `POST /api/admin/system/upload`

**变化：**
- 原来：返回错误提示
- 现在：实际上传图片到OSS，返回图片URL

**使用示例：**
```bash
curl -X POST "http://localhost:8080/api/admin/system/upload" \
  -H "Authorization: Bearer {token}" \
  -F "file=@image.jpg"
```

**响应：**
```json
{
  "code": 200,
  "message": "上传成功",
  "data": "https://your-bucket.oss-cn-hangzhou.aliyuncs.com/images/common/20250130/abc123.jpg"
}
```

#### 3.3 商品管理 - 图片上传
**接口：** `POST /api/admin/products/upload`

**变化：**
- 原来：返回错误提示
- 现在：实际上传图片到OSS，返回图片URL

**使用示例：**
```bash
curl -X POST "http://localhost:8080/api/admin/products/upload" \
  -H "Authorization: Bearer {token}" \
  -F "file=@product.jpg"
```

---

## ⚙️ 配置说明

### 配置文件位置

`backend/huakang-admin/src/main/resources/application.yml`

### 配置项说明

```yaml
aliyun:
  oss:
    endpoint: ${OSS_ENDPOINT:} # OSS地域节点
    access-key-id: ${OSS_ACCESS_KEY_ID:} # AccessKey ID
    access-key-secret: ${OSS_ACCESS_KEY_SECRET:} # AccessKey Secret
    bucket-name: ${OSS_BUCKET_NAME:} # 存储桶名称
    domain: ${OSS_DOMAIN:} # 文件访问域名（可选，CDN域名）
    path-prefix: ${OSS_PATH_PREFIX:images/} # 文件上传路径前缀
```

### 环境变量配置

在 `.env` 文件或系统环境变量中配置：

```bash
# 阿里云OSS配置
OSS_ENDPOINT=https://oss-cn-hangzhou.aliyuncs.com
OSS_ACCESS_KEY_ID=your-access-key-id
OSS_ACCESS_KEY_SECRET=your-access-key-secret
OSS_BUCKET_NAME=your-bucket-name
OSS_DOMAIN=https://your-cdn-domain.com  # 可选，如果使用CDN
OSS_PATH_PREFIX=images/  # 可选，默认images/
```

### 配置示例

#### 开发环境（.env文件）
```bash
OSS_ENDPOINT=https://oss-cn-hangzhou.aliyuncs.com
OSS_ACCESS_KEY_ID=LTAI5txxxxxxxxxxxxx
OSS_ACCESS_KEY_SECRET=xxxxxxxxxxxxxxxxxxxxxxxxxxxxx
OSS_BUCKET_NAME=huakang-dev
OSS_DOMAIN=https://huakang-dev.oss-cn-hangzhou.aliyuncs.com
```

#### 生产环境（环境变量）
```bash
export OSS_ENDPOINT=https://oss-cn-beijing.aliyuncs.com
export OSS_ACCESS_KEY_ID=LTAI5txxxxxxxxxxxxx
export OSS_ACCESS_KEY_SECRET=xxxxxxxxxxxxxxxxxxxxxxxxxxxxx
export OSS_BUCKET_NAME=huakang-prod
export OSS_DOMAIN=https://cdn.huakang.com  # CDN域名
```

---

## 📁 文件存储结构

### 存储路径规则

```
{pathPrefix}{folder}/{yyyyMMdd}/{uuid}.{ext}
```

### 实际示例

- **商品图片：** `images/products/20250130/abc123def456.jpg`
- **二维码：** `images/qrcode/20250130/xyz789uvw012.png`
- **通用图片：** `images/common/20250130/def456ghi789.jpg`

### 文件夹分类

| 文件夹 | 用途 | 接口 |
|--------|------|------|
| `products` | 商品图片 | `POST /api/admin/products/upload` |
| `qrcode` | 二维码 | `POST /api/admin/system/wechat-qrcode` |
| `common` | 通用图片 | `POST /api/admin/system/upload` |

---

## 🔒 安全特性

### 1. 文件类型验证

仅允许以下图片格式：
- `image/jpeg` / `image/jpg`
- `image/png`
- `image/gif`
- `image/webp`

### 2. 文件大小限制

- 最大文件大小：**10MB**
- 超过限制会抛出异常

### 3. 文件名安全

- 使用UUID生成唯一文件名，防止文件名冲突
- 自动提取文件扩展名
- 按日期分类存储，便于管理

---

## 📝 使用示例

### 示例1：上传商品主图

```java
// Controller中
@PostMapping("/products/upload")
public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
    String imageUrl = ossService.uploadImage(file, "products");
    return Result.success("上传成功", imageUrl);
}
```

### 示例2：上传企业微信二维码

```java
// Controller中
@PostMapping("/system/wechat-qrcode")
public Result<SystemConfigVO> uploadWechatQrcode(@RequestParam("file") MultipartFile file) {
    // 上传到OSS
    String imageUrl = ossService.uploadImage(file, "qrcode");
    // 保存到系统配置
    SystemConfigVO config = systemConfigService.uploadWechatQrcode(imageUrl);
    return Result.success("上传成功", config);
}
```

### 示例3：删除文件

```java
// 删除OSS中的文件
boolean deleted = ossService.deleteFile("https://your-bucket.oss-cn-hangzhou.aliyuncs.com/images/products/20250130/abc123.jpg");
```

---

## ⚠️ 注意事项

### 1. OSS未配置时的处理

如果OSS配置为空，调用上传接口会抛出异常：
```
OSS未配置，请先配置阿里云OSS相关信息
```

**解决方案：**
- 配置环境变量或application.yml中的OSS信息
- 或使用本地存储（需要额外实现）

### 2. 文件访问域名

- 如果配置了 `domain`，使用配置的域名（通常是CDN域名）
- 如果未配置，使用OSS默认域名：`https://{bucket}.{endpoint}/{filePath}`

### 3. 权限要求

所有上传接口都需要：
- ✅ 管理员权限（`@RequireRole("admin")`）
- ✅ JWT Token认证

---

## 🚀 快速开始

### 1. 配置OSS信息

在 `application.yml` 或环境变量中配置OSS信息。

### 2. 启动应用

```bash
cd backend/huakang-admin
mvn spring-boot:run
```

### 3. 测试上传

使用Postman或curl测试上传接口：

```bash
curl -X POST "http://localhost:8080/api/admin/products/upload" \
  -H "Authorization: Bearer {your-jwt-token}" \
  -F "file=@test.jpg"
```

---

## 📊 接口列表

| 接口路径 | 方法 | 说明 | 文件夹 |
|---------|------|------|--------|
| `/api/admin/products/upload` | POST | 上传商品图片 | `products` |
| `/api/admin/system/upload` | POST | 上传通用图片 | `common` |
| `/api/admin/system/wechat-qrcode` | POST | 上传企业微信二维码 | `qrcode` |

---

## 🔧 故障排查

### 问题1：上传失败，提示"OSS未配置"

**原因：** OSS配置信息未填写或配置错误

**解决：**
1. 检查 `application.yml` 中的OSS配置
2. 检查环境变量是否正确设置
3. 确认AccessKey ID和Secret是否正确

### 问题2：上传失败，提示"不支持的文件类型"

**原因：** 上传的文件不是图片格式

**解决：** 确保上传的是JPG、PNG、GIF或WebP格式的图片

### 问题3：上传失败，提示"文件大小不能超过10MB"

**原因：** 文件超过10MB限制

**解决：** 压缩图片或调整 `MAX_FILE_SIZE` 常量（不推荐）

---

## 📌 总结

✅ **已完成：**
- OSS SDK集成
- 图片上传服务实现
- 三个上传接口更新
- 配置管理完善

✅ **功能特性：**
- 文件格式验证
- 文件大小限制
- 自动生成唯一文件名
- 按日期和文件夹分类
- 支持CDN域名配置

✅ **安全特性：**
- 仅管理员可访问
- JWT Token认证
- 文件类型白名单
