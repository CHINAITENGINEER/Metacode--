@echo off
REM 华康电器连锁积分小程序 - Windows部署脚本
REM 用途：一键部署到生产服务器（Windows版本）

echo ==========================================
echo 华康电器连锁积分小程序 - 部署脚本
echo ==========================================
echo.

REM 检查环境变量
echo [1/7] 检查环境变量...
if "%JWT_SECRET%"=="" (
    echo 错误: JWT_SECRET 环境变量未设置
    echo 请先设置环境变量，例如：
    echo set JWT_SECRET=your-strong-jwt-secret-key-at-least-64-characters-long
    exit /b 1
)

if "%REDIS_PASSWORD%"=="" (
    echo 错误: REDIS_PASSWORD 环境变量未设置
    echo 请先设置环境变量，例如：
    echo set REDIS_PASSWORD=your-strong-redis-password
    exit /b 1
)
echo √ 环境变量检查通过
echo.

REM 检查Docker
echo [2/7] 检查Docker环境...
docker --version >nul 2>&1
if errorlevel 1 (
    echo 错误: Docker 未安装或未启动
    exit /b 1
)
echo √ Docker环境检查通过
echo.

REM 备份数据库
echo [3/7] 备份数据库...
if not exist backups mkdir backups
set BACKUP_FILE=backups\backup_%date:~0,4%%date:~5,2%%date:~8,2%_%time:~0,2%%time:~3,2%%time:~6,2%.sql
docker ps | findstr hk-mysql >nul 2>&1
if not errorlevel 1 (
    echo 正在备份数据库...
    docker exec hk-mysql mysqldump -uroot -p%MYSQL_ROOT_PASSWORD% %MYSQL_DATABASE% > %BACKUP_FILE%
    echo √ 数据库备份完成
) else (
    echo ⚠ MySQL容器未运行，跳过备份
)
echo.

REM 停止旧服务
echo [4/7] 停止旧服务...
docker-compose down
echo √ 旧服务已停止
echo.

REM 构建镜像
echo [5/7] 构建Docker镜像...
docker-compose build --no-cache
if errorlevel 1 (
    echo 错误: 镜像构建失败
    exit /b 1
)
echo √ 镜像构建完成
echo.

REM 启动服务
echo [6/7] 启动服务...
docker-compose up -d
if errorlevel 1 (
    echo 错误: 服务启动失败
    exit /b 1
)
echo √ 服务启动完成
echo.

REM 健康检查
echo [7/7] 健康检查...
echo 等待服务启动（60秒）...
timeout /t 60 /nobreak >nul

curl -f http://localhost:%ADMIN_PORT%/api/admin/health/ping >nul 2>&1
if not errorlevel 1 (
    echo √ Admin服务健康
) else (
    echo × Admin服务不健康
)

curl -f http://localhost:%MINIAPP_PORT%/api/miniapp/health/ping >nul 2>&1
if not errorlevel 1 (
    echo √ Miniapp服务健康
) else (
    echo × Miniapp服务不健康
)

echo.
echo 服务状态：
docker-compose ps

echo.
echo ==========================================
echo 部署完成！
echo ==========================================
echo.
echo 服务访问地址：
echo   - Admin API: http://localhost:%ADMIN_PORT%/api/admin
echo   - Miniapp API: http://localhost:%MINIAPP_PORT%/api/miniapp
echo   - Admin文档: http://localhost:%ADMIN_PORT%/api/admin/doc.html
echo.
echo 查看日志：
echo   docker-compose logs -f admin
echo   docker-compose logs -f miniapp
echo.

pause
