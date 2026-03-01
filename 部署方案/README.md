# Linux 云服务器部署方案 - 总览

## 📋 部署文档目录

本部署方案包含以下文档，请按顺序阅读和执行：

### 1. [OSS 配置指南](./1-OSS配置指南.md) ⭐ 重点
- 阿里云 OSS 服务开通
- 创建 Bucket 和配置权限
- 获取 AccessKey 密钥
- 配置跨域规则
- 项目中的 OSS 配置
- 测试验证上传功能

### 2. [服务器环境准备](./2-服务器环境准备.md)
- 服务器选购建议
- 连接服务器
- 安装 JDK 17
- 安装 MySQL 8.0
- 安装 Redis
- 安装 Nginx
- 配置防火墙

### 3. [项目部署步骤](./3-项目部署步骤.md)
- 打包项目
- 上传到服务器
- 配置环境变量
- 启动项目
- 配置开机自启
- 日志管理

### 4. [Nginx 配置](./4-Nginx配置.md)
- 反向代理配置
- HTTPS 配置
- 性能优化
- 安全配置
- 日志分析

---

## 🚀 快速开始

### 前置条件

- ✅ 已购买云服务器（推荐配置：2核4G，40GB SSD）
- ✅ 已注册阿里云账号
- ✅ 已准备域名（可选，用于 HTTPS）

### 部署流程

```
1. 配置 OSS（解决文件上传问题）
   ↓
2. 准备服务器环境（安装 JDK、MySQL、Redis、Nginx）
   ↓
3. 部署项目（打包、上传、启动）
   ↓
4. 配置 Nginx（反向代理、HTTPS）
   ↓
5. 测试验证
```

---

## 🎯 核心问题解决

### 问题1：OSS 上传配置 ⭐

**问题描述：** 项目需要上传图片和视频到阿里云 OSS

**解决方案：**

1. **开通 OSS 服务**
   - 登录阿里云控制台
   - 开通对象存储 OSS 服务
   - 创建 Bucket（如：`huakang-electronics`）

2. **获取访问密钥**
   - 创建 RAM 用户（推荐）
   - 授予 OSS 权限
   - 获取 AccessKey ID 和 AccessKey Secret

3. **配置项目**

在服务器上创建 `.env` 文件：

```bash
# OSS配置
OSS_ENDPOINT=https://oss-cn-hangzhou.aliyuncs.com
OSS_ACCESS_KEY_ID=LTAI5t********************
OSS_ACCESS_KEY_SECRET=3Xj8****************************
OSS_BUCKET_NAME=huakang-electronics
OSS_DOMAIN=https://huakang-electronics.oss-cn-hangzhou.aliyuncs.com
OSS_PATH_PREFIX=images/
```

4. **测试上传**

```bash
# 启动项目后测试
curl -X POST http://localhost:8080/api/admin/oss/upload/media \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "files=@test.jpg" \
  -F "folder=products"
```

**详细步骤：** 参考 [1-OSS配置指南.md](./1-OSS配置指南.md)

---

## 📦 部署架构

```
┌─────────────────────────────────────────────────────────┐
│                      用户/客户端                          │
└─────────────────────────────────────────────────────────┘
                            │
                            ↓
┌─────────────────────────────────────────────────────────┐
│                    Nginx (80/443)                        │
│  - 反向代理                                               │
│  - HTTPS 加密                                            │
│  - 静态文件服务                                           │
└─────────────────────────────────────────────────────────┘
                            │
                            ↓
┌─────────────────────────────────────────────────────────┐
│              Spring Boot 应用 (8080)                     │
│  - 后台管理 API                                          │
│  - 小程序 API                                            │
└─────────────────────────────────────────────────────────┘
                            │
                ┌───────────┼───────────┐
                ↓           ↓           ↓
        ┌──────────┐  ┌──────────┐  ┌──────────┐
        │  MySQL   │  │  Redis   │  │ 阿里云OSS │
        │  (3306)  │  │  (6379)  │  │ (文件存储)│
        └──────────┘  └──────────┘  └──────────┘
```

---

## 🔧 环境配置清单

### 服务器配置

| 组件 | 版本 | 端口 | 说明 |
|------|------|------|------|
| 操作系统 | CentOS 7.9 / Ubuntu 20.04 | - | 推荐 CentOS 7.9 |
| JDK | 17 | - | OpenJDK 17 |
| MySQL | 8.0 | 3306 | 数据库 |
| Redis | 6.x | 6379 | 缓存 |
| Nginx | 1.x | 80/443 | 反向代理 |
| Spring Boot | 3.x | 8080 | 应用服务 |

### 阿里云 OSS 配置

| 配置项 | 说明 | 示例 |
|--------|------|------|
| Endpoint | OSS 地域节点 | `https://oss-cn-hangzhou.aliyuncs.com` |
| Bucket | 存储桶名称 | `huakang-electronics` |
| AccessKey ID | 访问密钥 ID | `LTAI5t********************` |
| AccessKey Secret | 访问密钥 Secret | `3Xj8****************************` |
| Domain | 访问域名 | `https://huakang-electronics.oss-cn-hangzhou.aliyuncs.com` |

---

## 📝 环境变量配置

创建 `/opt/huakang/.env` 文件：

```bash
# 数据库配置
DB_HOST=localhost
DB_PORT=3306
DB_NAME=hk_electronics
DB_USERNAME=huakang
DB_PASSWORD=HuaKang2024!

# Redis配置
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=YourRedisPassword123!

# JWT配置
JWT_SECRET=huakang-electronics-prod-secret-key-2024-very-long-and-secure
JWT_EXPIRATION=7200000

# OSS配置（重点）
OSS_ENDPOINT=https://oss-cn-hangzhou.aliyuncs.com
OSS_ACCESS_KEY_ID=LTAI5t********************
OSS_ACCESS_KEY_SECRET=3Xj8****************************
OSS_BUCKET_NAME=huakang-electronics
OSS_DOMAIN=https://huakang-electronics.oss-cn-hangzhou.aliyuncs.com
OSS_PATH_PREFIX=images/

# 服务器配置
SERVER_PORT=8080
```

---

## ✅ 部署检查清单

### 1. OSS 配置检查

- [ ] OSS 服务已开通
- [ ] Bucket 已创建
- [ ] 读写权限设置为"公共读"
- [ ] 跨域规则已配置
- [ ] AccessKey 已获取
- [ ] 项目中 OSS 配置已填写
- [ ] 上传功能测试通过

### 2. 服务器环境检查

- [ ] JDK 17 已安装
- [ ] MySQL 8.0 已安装并启动
- [ ] Redis 已安装并设置密码
- [ ] Nginx 已安装并启动
- [ ] 防火墙端口已开放
- [ ] 云服务器安全组已配置

### 3. 项目部署检查

- [ ] 项目已打包成 JAR 文件
- [ ] JAR 文件已上传到服务器
- [ ] 环境变量文件已创建
- [ ] 数据库已导入
- [ ] 项目已启动
- [ ] 健康检查接口正常
- [ ] 开机自启已配置

### 4. Nginx 配置检查

- [ ] 反向代理已配置
- [ ] HTTPS 证书已配置（可选）
- [ ] 静态文件路径正确
- [ ] Gzip 压缩已启用
- [ ] 安全头已配置

---

## 🧪 测试验证

### 1. 健康检查

```bash
curl http://localhost:8080/api/admin/health
```

**预期返回：**
```json
{"status":"UP"}
```

### 2. 登录接口测试

```bash
curl -X POST http://localhost:8080/api/admin/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

### 3. OSS 上传测试

```bash
# 先登录获取 token
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

# 测试上传
curl -X POST http://localhost:8080/api/admin/oss/upload/media \
  -H "Authorization: Bearer $TOKEN" \
  -F "files=@test.jpg" \
  -F "folder=products"
```

**预期返回：**
```json
{
  "code": 200,
  "message": "OK",
  "data": [
    "https://huakang-electronics.oss-cn-hangzhou.aliyuncs.com/images/products/20260215/xxx.jpg"
  ]
}
```

### 4. 通过 Nginx 访问

```bash
curl http://your-domain.com/api/admin/health
```

---

## 🔍 常见问题排查

### 问题1：OSS 上传失败

**错误信息：** `OSS未配置，请先配置阿里云OSS相关信息`

**解决方案：**
1. 检查 `.env` 文件中的 OSS 配置是否完整
2. 确认环境变量已加载
3. 重启项目

### 问题2：无法连接数据库

**错误信息：** `Communications link failure`

**解决方案：**
1. 检查 MySQL 是否启动：`sudo systemctl status mysqld`
2. 测试数据库连接：`mysql -h localhost -u huakang -p`
3. 检查防火墙是否阻止连接

### 问题3：502 Bad Gateway

**原因：** 后端服务未启动

**解决方案：**
1. 检查项目是否启动：`sudo systemctl status huakang-admin`
2. 查看日志：`tail -f /opt/huakang/logs/application.log`
3. 重启项目：`sudo systemctl restart huakang-admin`

### 问题4：HTTPS 证书错误

**解决方案：**
1. 检查证书文件路径是否正确
2. 检查证书是否过期：`openssl x509 -in cert.pem -noout -dates`
3. 重新申请证书

---

## 📊 性能优化建议

### 1. JVM 参数优化

```bash
# 根据服务器内存调整
-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200
```

### 2. MySQL 优化

```ini
# /etc/my.cnf
max_connections=500
innodb_buffer_pool_size=512M
query_cache_size=64M
```

### 3. Redis 优化

```ini
# /etc/redis.conf
maxmemory 1gb
maxmemory-policy allkeys-lru
```

### 4. Nginx 优化

```nginx
worker_processes auto;
worker_connections 2048;
gzip on;
gzip_comp_level 6;
```

---

## 🔒 安全建议

1. **修改默认密码**
   - MySQL root 密码
   - Redis 密码
   - 管理员账号密码

2. **配置防火墙**
   - 只开放必要的端口
   - 限制 MySQL 和 Redis 只允许内网访问

3. **使用 HTTPS**
   - 申请 SSL 证书
   - 强制 HTTP 重定向到 HTTPS

4. **定期备份**
   - 数据库定期备份
   - 配置文件备份
   - 日志归档

5. **监控告警**
   - 配置服务监控
   - 设置异常告警
   - 定期查看日志

---

## 📞 技术支持

如果在部署过程中遇到问题，请：

1. 查看对应文档的"常见问题"章节
2. 检查日志文件：
   - 应用日志：`/opt/huakang/logs/application.log`
   - Nginx 日志：`/var/log/nginx/huakang-error.log`
   - MySQL 日志：`/var/log/mysqld.log`
3. 使用健康检查接口诊断问题

---

## 📚 相关资源

- [Spring Boot 官方文档](https://spring.io/projects/spring-boot)
- [MySQL 官方文档](https://dev.mysql.com/doc/)
- [Redis 官方文档](https://redis.io/documentation)
- [Nginx 官方文档](https://nginx.org/en/docs/)
- [阿里云 OSS 文档](https://help.aliyun.com/product/31815.html)

---

**文档版本**：v1.0  
**更新日期**：2026-02-15  
**适用项目**：华康电器积分系统  
**维护者**：开发团队
