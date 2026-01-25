# Docker端口冲突解决方案

## 🔍 问题分析

**错误信息：**
```
Error response from daemon: ports are not available: exposing port TCP 0.0.0.0:3306 -> 127.0.0.1:0: listen tcp 0.0.0.0:3306: bind: Only one usage of each socket address (protocol/network address/port) is normally permitted.
```

**原因：** 端口3306已被占用（可能是本地MySQL服务）

**当前状态：**
- ✅ Redis容器已成功启动
- ❌ MySQL容器无法启动（端口冲突）

---

## ✅ 解决方案

### 方案一：修改Docker端口映射（推荐，最简单）

**优点：** 不需要停止本地MySQL，两者可以共存

#### 步骤1：修改docker-compose.yml

将MySQL端口从3306改为3307：

```yaml
ports:
  - "${MYSQL_PORT:-3307}:3306"  # 改为3307
```

#### 步骤2：更新.env文件

创建或编辑 `.env` 文件：

```env
MYSQL_PORT=3307
```

#### 步骤3：重启服务

```bash
# 停止现有容器
docker compose down

# 重新启动
docker compose up -d
```

#### 步骤4：更新应用配置

如果后端应用已配置，需要更新数据库连接：

```yaml
# application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3307/hk_electronics
```

---

### 方案二：停止本地MySQL服务

**适用场景：** 如果不需要本地MySQL，只使用Docker中的MySQL

#### Windows服务方式：

```powershell
# 停止MySQL服务
net stop MySQL80

# 或者使用服务管理器
services.msc
# 找到MySQL服务，右键停止
```

#### 如果MySQL是手动启动的：

```powershell
# 查找MySQL进程
tasklist | findstr mysql

# 结束进程（替换PID为实际进程ID）
taskkill /PID <进程ID> /F
```

#### 然后重启Docker服务：

```bash
docker compose restart mysql
```

---

### 方案三：使用本地MySQL（不启动Docker MySQL）

**适用场景：** 如果本地MySQL已有数据，想直接使用

#### 步骤1：注释掉docker-compose.yml中的MySQL服务

```yaml
# mysql:
#   image: mysql:8.0
#   ...
```

#### 步骤2：直接使用本地MySQL

确保本地MySQL已创建数据库 `hk_electronics`，然后执行初始化脚本。

---

## 🚀 快速修复（推荐方案一）

我已经为你准备好了修改后的配置，直接执行：

```bash
# 1. 停止现有容器
docker compose down

# 2. 修改端口（已自动完成，见下方）
# 3. 重新启动
docker compose up -d
```

---

## 📝 验证修复

修复后，验证服务状态：

```bash
# 查看容器状态
docker compose ps

# 查看MySQL日志
docker compose logs mysql

# 测试MySQL连接（端口3307）
docker compose exec mysql mysql -uroot -proot -e "SELECT 1;"
```

---

## ⚠️ 注意事项

1. **端口修改后**，后端应用的数据库连接配置也需要相应修改
2. **Navicat等工具**连接时，端口改为3307
3. **数据持久化**：Docker MySQL的数据存储在卷中，不会丢失

---

**推荐使用方案一，简单快速！** 🎉
