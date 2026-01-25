# Docker Compose 配置指南

## 📋 目录

1. [环境准备](#环境准备)
2. [配置文件说明](#配置文件说明)
3. [快速启动](#快速启动)
4. [服务配置详解](#服务配置详解)
5. [常见问题](#常见问题)
6. [生产环境配置](#生产环境配置)

---

## 🔧 环境准备

### 1. 安装 Docker Desktop（Windows）

1. **下载 Docker Desktop**
   - 访问：https://www.docker.com/products/docker-desktop/
   - 下载 Windows 版本并安装

2. **启动 Docker Desktop**
   - 安装完成后，启动 Docker Desktop
   - 等待 Docker 引擎启动完成（系统托盘图标变为绿色）

3. **验证安装**
   ```bash
   docker --version
   docker compose version
   ```

### 2. 检查端口占用

确保以下端口未被占用：
- **3306** - MySQL
- **6379** - Redis

检查命令（PowerShell）：
```powershell
netstat -ano | findstr :3306
netstat -ano | findstr :6379
```

如果端口被占用，可以：
- 修改 `env.example` 中的端口配置
- 或停止占用端口的服务

---

## 📝 配置文件说明

### 1. `docker-compose.yml`

这是 Docker Compose 的主配置文件，定义了：
- **MySQL 服务**：数据库容器
- **Redis 服务**：缓存容器
- **后端服务**：已注释，等后端代码完成后启用

### 2. `env.example`

环境变量示例文件，包含：
- MySQL 配置（密码、数据库名、端口等）
- Redis 配置（端口）
- 后端配置（端口，暂未使用）

**重要**：需要复制为 `.env` 文件才能使用！

---

## 🚀 快速启动

### 步骤 1：创建环境变量文件

在项目根目录下，复制 `env.example` 为 `.env`：

**Windows PowerShell：**
```powershell
Copy-Item env.example .env
```

**Windows CMD：**
```cmd
copy env.example .env
```

**或者手动创建**：
创建 `.env` 文件，内容如下：
```env
MYSQL_ROOT_PASSWORD=root
MYSQL_DATABASE=hk_electronics
MYSQL_USER=hk
MYSQL_PASSWORD=hk123456
MYSQL_PORT=3306

REDIS_PORT=6379

# 后端启用后可用
BACKEND_PORT=3000
```

### 步骤 2：启动服务

在项目根目录下执行：

```bash
docker compose up -d
```

**参数说明：**
- `up`：启动服务
- `-d`：后台运行（detached mode）

### 步骤 3：查看服务状态

```bash
docker compose ps
```

**预期输出：**
```
NAME         IMAGE              STATUS          PORTS
hk-mysql     mysql:8.0          Up 30 seconds   0.0.0.0:3306->3306/tcp
hk-redis     redis:7-alpine     Up 30 seconds   0.0.0.0:6379->6379/tcp
```

### 步骤 4：查看日志

查看所有服务日志：
```bash
docker compose logs -f
```

查看特定服务日志：
```bash
docker compose logs -f mysql
docker compose logs -f redis
```

### 步骤 5：验证数据库初始化

等待 MySQL 完全启动（约 30-60 秒），然后验证：

```bash
# 进入 MySQL 容器
docker compose exec mysql mysql -uroot -proot

# 在 MySQL 中执行
USE hk_electronics;
SHOW TABLES;
SELECT * FROM system_configs;
```

**预期结果：**
- 看到 6 张表：admins, staffs, system_configs, members, products, points_records
- 看到 2 个视图：v_member_points_stats, v_dashboard_stats
- 看到 3 条配置数据

---

## 🔍 服务配置详解

### MySQL 服务

**配置项：**
- **镜像**：`mysql:8.0`
- **容器名**：`hk-mysql`
- **端口映射**：`3306:3306`（可通过 `.env` 修改）
- **数据持久化**：`mysql_data` 卷（数据不会丢失）
- **自动初始化**：自动执行 `database_init_enterprise_fixed.sql`

**连接信息：**
- **主机**：`localhost` 或 `127.0.0.1`
- **端口**：`3306`（或 `.env` 中配置的端口）
- **用户名**：`root`（或 `.env` 中的 `MYSQL_USER`）
- **密码**：`.env` 中的 `MYSQL_ROOT_PASSWORD`
- **数据库**：`hk_electronics`

**健康检查：**
- 每 5 秒检查一次
- 最多重试 30 次（约 2.5 分钟）

### Redis 服务

**配置项：**
- **镜像**：`redis:7-alpine`（轻量级版本）
- **容器名**：`hk-redis`
- **端口映射**：`6379:6379`（可通过 `.env` 修改）
- **数据持久化**：`redis_data` 卷（AOF 持久化）

**连接信息：**
- **主机**：`localhost` 或 `127.0.0.1`
- **端口**：`6379`（或 `.env` 中配置的端口）
- **密码**：无（生产环境建议设置密码）

**健康检查：**
- 每 5 秒检查一次
- 最多重试 30 次

---

## 🛠️ 常用命令

### 启动服务
```bash
docker compose up -d
```

### 停止服务
```bash
docker compose stop
```

### 停止并删除容器
```bash
docker compose down
```

### 停止并删除容器和数据卷（⚠️ 危险：会删除数据）
```bash
docker compose down -v
```

### 重启服务
```bash
docker compose restart
```

### 重启特定服务
```bash
docker compose restart mysql
docker compose restart redis
```

### 查看服务状态
```bash
docker compose ps
```

### 查看日志
```bash
# 所有服务
docker compose logs -f

# 特定服务
docker compose logs -f mysql
docker compose logs -f redis
```

### 进入容器
```bash
# 进入 MySQL 容器
docker compose exec mysql bash

# 进入 Redis 容器
docker compose exec redis sh
```

### 执行 SQL
```bash
# 方式1：直接执行 SQL 文件
docker compose exec -T mysql mysql -uroot -proot hk_electronics < your_file.sql

# 方式2：进入 MySQL 命令行
docker compose exec mysql mysql -uroot -proot
```

### 备份数据库
```bash
# 备份整个数据库
docker compose exec mysql mysqldump -uroot -proot hk_electronics > backup.sql

# 备份所有数据库
docker compose exec mysql mysqldump -uroot -proot --all-databases > all_backup.sql
```

### 恢复数据库
```bash
# 恢复数据库
docker compose exec -T mysql mysql -uroot -proot hk_electronics < backup.sql
```

---

## ❓ 常见问题

### 1. 端口已被占用

**错误信息：**
```
Error: bind: address already in use
```

**解决方案：**
1. 修改 `.env` 文件中的端口配置：
   ```env
   MYSQL_PORT=3307
   REDIS_PORT=6380
   ```
2. 或停止占用端口的服务

### 2. MySQL 初始化失败

**问题：** 数据库表没有创建

**解决方案：**
1. 检查 `database_init_enterprise_fixed.sql` 文件是否存在
2. 查看 MySQL 日志：
   ```bash
   docker compose logs mysql
   ```
3. 手动执行初始化脚本：
   ```bash
   docker compose exec -T mysql mysql -uroot -proot < database_init_enterprise_fixed.sql
   ```

### 3. 容器启动失败

**检查步骤：**
1. 查看容器状态：
   ```bash
   docker compose ps
   ```
2. 查看日志：
   ```bash
   docker compose logs
   ```
3. 检查 Docker 是否运行：
   ```bash
   docker ps
   ```

### 4. 数据丢失

**原因：** 执行了 `docker compose down -v`，删除了数据卷

**预防：**
- 定期备份数据库
- 不要使用 `-v` 参数删除数据卷

**恢复：**
- 如果有备份，使用备份恢复
- 如果没有备份，数据无法恢复

### 5. 无法连接到 MySQL

**检查步骤：**
1. 确认容器正在运行：
   ```bash
   docker compose ps
   ```
2. 检查端口映射：
   ```bash
   docker compose port mysql 3306
   ```
3. 测试连接：
   ```bash
   docker compose exec mysql mysql -uroot -proot -e "SELECT 1;"
   ```

### 6. Windows 路径问题

**问题：** Windows 中文路径可能导致挂载失败

**解决方案：**
- 使用英文路径
- 或使用 WSL2（Windows Subsystem for Linux）

---

## 🏭 生产环境配置

### 1. 修改默认密码

**重要**：生产环境必须修改默认密码！

编辑 `.env` 文件：
```env
MYSQL_ROOT_PASSWORD=你的强密码
MYSQL_PASSWORD=你的强密码
```

### 2. 配置 Redis 密码

修改 `docker-compose.yml` 中的 Redis 配置：

```yaml
redis:
  image: redis:7-alpine
  container_name: hk-redis
  restart: unless-stopped
  ports:
    - "${REDIS_PORT:-6379}:6379"
  volumes:
    - redis_data:/data
  command: ["redis-server", "--appendonly", "yes", "--requirepass", "${REDIS_PASSWORD:-your_redis_password}"]
```

在 `.env` 中添加：
```env
REDIS_PASSWORD=你的Redis密码
```

### 3. 限制资源使用

在 `docker-compose.yml` 中添加资源限制：

```yaml
mysql:
  # ... 其他配置
  deploy:
    resources:
      limits:
        cpus: '2'
        memory: 2G
      reservations:
        cpus: '1'
        memory: 1G

redis:
  # ... 其他配置
  deploy:
    resources:
      limits:
        cpus: '1'
        memory: 512M
      reservations:
        cpus: '0.5'
        memory: 256M
```

### 4. 配置数据备份

创建备份脚本 `backup.sh`：

```bash
#!/bin/bash
BACKUP_DIR="./backups"
DATE=$(date +%Y%m%d_%H%M%S)

mkdir -p $BACKUP_DIR

# 备份 MySQL
docker compose exec -T mysql mysqldump -uroot -p$MYSQL_ROOT_PASSWORD hk_electronics > $BACKUP_DIR/mysql_$DATE.sql

# 备份 Redis（可选）
docker compose exec redis redis-cli --rdb $BACKUP_DIR/redis_$DATE.rdb

echo "备份完成: $BACKUP_DIR"
```

### 5. 使用外部数据库（可选）

如果使用云数据库（如阿里云 RDS），可以：
1. 注释掉 `docker-compose.yml` 中的 MySQL 服务
2. 在 `.env` 中配置外部数据库连接信息
3. 后端应用直接连接外部数据库

---

## 📊 监控和维护

### 查看资源使用情况

```bash
docker stats
```

### 清理未使用的资源

```bash
# 清理未使用的镜像
docker image prune -a

# 清理未使用的卷
docker volume prune

# 清理所有未使用的资源
docker system prune -a
```

### 更新镜像

```bash
# 拉取最新镜像
docker compose pull

# 重新创建容器
docker compose up -d --force-recreate
```

---

## ✅ 验证清单

启动后，请验证以下内容：

- [ ] MySQL 容器运行正常
- [ ] Redis 容器运行正常
- [ ] 可以通过 Navicat/DBeaver 连接到 MySQL
- [ ] 数据库 `hk_electronics` 已创建
- [ ] 所有表已创建（6 张表）
- [ ] 视图已创建（2 个视图）
- [ ] 配置数据已插入（3 条）
- [ ] 可以通过 Redis 客户端连接
- [ ] 日志无错误信息

---

## 📚 相关文档

- [Docker 官方文档](https://docs.docker.com/)
- [Docker Compose 文档](https://docs.docker.com/compose/)
- [MySQL Docker 镜像](https://hub.docker.com/_/mysql)
- [Redis Docker 镜像](https://hub.docker.com/_/redis)

---

## 🆘 获取帮助

如果遇到问题：
1. 查看日志：`docker compose logs`
2. 检查容器状态：`docker compose ps`
3. 查看 Docker 文档
4. 联系技术支持

---

**配置完成后，你的开发环境就准备好了！** 🎉
