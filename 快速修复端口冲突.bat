@echo off
chcp 65001 >nul
echo ========================================
echo    Docker端口冲突快速修复
echo ========================================
echo.

echo [1/3] 停止现有容器...
docker compose down
if errorlevel 1 (
    echo [错误] 停止容器失败
    pause
    exit /b 1
)
echo [✓] 容器已停止

echo.
echo [2/3] 检查.env文件...
if not exist .env (
    copy env.example .env >nul
    echo [✓] .env文件已创建，端口已设置为3307
) else (
    echo [!] .env文件已存在
    echo 请确保MYSQL_PORT=3307
)

echo.
echo [3/3] 重新启动服务（使用端口3307）...
docker compose up -d
if errorlevel 1 (
    echo [错误] 启动失败
    pause
    exit /b 1
)

echo.
echo ========================================
echo    修复完成！
echo ========================================
echo.
echo MySQL现在使用端口: 3307
echo Redis使用端口: 6379
echo.
echo 查看服务状态: docker compose ps
echo 查看日志: docker compose logs -f
echo.
pause
