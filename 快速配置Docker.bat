@echo off
chcp 65001 >nul
echo ========================================
echo    Docker Compose 快速配置脚本
echo ========================================
echo.

REM 检查 Docker 是否安装
docker --version >nul 2>&1
if errorlevel 1 (
    echo [错误] Docker 未安装或未启动
    echo 请先安装 Docker Desktop: https://www.docker.com/products/docker-desktop/
    pause
    exit /b 1
)

echo [1/4] 检查 Docker 状态...
docker ps >nul 2>&1
if errorlevel 1 (
    echo [错误] Docker 服务未运行
    echo 请启动 Docker Desktop
    pause
    exit /b 1
)
echo [✓] Docker 运行正常

echo.
echo [2/4] 创建环境变量文件...
if not exist .env (
    copy env.example .env >nul
    echo [✓] .env 文件已创建
) else (
    echo [!] .env 文件已存在，跳过创建
)

echo.
echo [3/4] 检查端口占用...
netstat -ano | findstr :3306 >nul
if not errorlevel 1 (
    echo [!] 警告: 端口 3306 已被占用，可能需要修改 .env 中的 MYSQL_PORT
)
netstat -ano | findstr :6379 >nul
if not errorlevel 1 (
    echo [!] 警告: 端口 6379 已被占用，可能需要修改 .env 中的 REDIS_PORT
)

echo.
echo [4/4] 启动 Docker Compose 服务...
echo.
docker compose up -d

if errorlevel 1 (
    echo.
    echo [错误] 启动失败，请检查日志: docker compose logs
    pause
    exit /b 1
)

echo.
echo ========================================
echo    配置完成！
echo ========================================
echo.
echo 服务状态: docker compose ps
echo 查看日志: docker compose logs -f
echo 停止服务: docker compose stop
echo.
echo 等待 30-60 秒后，MySQL 将自动初始化数据库...
echo.
pause
