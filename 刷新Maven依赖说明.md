# 刷新 Maven 依赖说明

## 问题
pom.xml 已更新为 Spring Boot 3.1.5，但运行时仍使用 3.2.0，需要强制刷新 Maven 依赖。

## 解决方案

### 方法 1：在 IntelliJ IDEA 中刷新（推荐）

1. **打开 Maven 工具窗口**
   - 点击右侧边栏的 "Maven" 图标
   - 或菜单：`View` → `Tool Windows` → `Maven`

2. **刷新项目**
   - 在 Maven 工具窗口中，点击刷新按钮（🔄 Reload All Maven Projects）
   - 或右键点击项目根目录 → `Maven` → `Reload Project`

3. **清理并重新编译**
   - 菜单：`Build` → `Rebuild Project`
   - 或使用快捷键：`Ctrl + Shift + F9`（Windows）

### 方法 2：删除本地缓存（如果方法1不行）

1. **关闭 IntelliJ IDEA**

2. **删除 Maven 本地仓库中的 Spring Boot 3.2.0 缓存**
   - 位置：`C:\Users\windows\.m2\repository\org\springframework\boot\`
   - 删除 `spring-boot-dependencies\3.2.0` 目录
   - 删除 `spring-boot-starter-*\3.2.0` 相关目录

3. **重新打开 IntelliJ IDEA**
   - 打开项目
   - 等待 Maven 自动下载依赖

### 方法 3：使用 Maven 命令行（如果有 Maven）

```bash
cd backend
mvn clean install -U
```

`-U` 参数会强制更新所有依赖。

## 验证

刷新后，检查 classpath 中是否包含 Spring Boot 3.1.5：
- 在 Maven 工具窗口中查看依赖树
- 或运行应用，查看启动日志中的版本号

## 注意事项

- 刷新可能需要几分钟时间下载依赖
- 确保网络连接正常
- 如果下载失败，可能需要配置 Maven 镜像源
