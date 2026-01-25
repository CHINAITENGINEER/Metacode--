# Docker Compose 快速配置脚本 (PowerShell)
# 编码: UTF-8

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   Docker Compose 快速配置脚本" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 检查 Docker 是否安装
Write-Host "[1/4] 检查 Docker 安装..." -ForegroundColor Yellow
try {
    $dockerVersion = docker --version 2>&1
    if ($LASTEXITCODE -ne 0) {
        throw "Docker 未安装"
    }
    Write-Host "[✓] Docker 已安装: $dockerVersion" -ForegroundColor Green
} catch {
    Write-Host "[错误] Docker 未安装或未启动" -ForegroundColor Red
    Write-Host "请先安装 Docker Desktop: https://www.docker.com/products/docker-desktop/" -ForegroundColor Yellow
    Read-Host "按 Enter 键退出"
    exit 1
}

# 检查 Docker 服务是否运行
Write-Host ""
Write-Host "[2/4] 检查 Docker 服务状态..." -ForegroundColor Yellow
try {
    docker ps | Out-Null
    if ($LASTEXITCODE -ne 0) {
        throw "Docker 服务未运行"
    }
    Write-Host "[✓] Docker 服务运行正常" -ForegroundColor Green
} catch {
    Write-Host "[错误] Docker 服务未运行" -ForegroundColor Red
    Write-Host "请启动 Docker Desktop" -ForegroundColor Yellow
    Read-Host "按 Enter 键退出"
    exit 1
}

# 创建环境变量文件
Write-Host ""
Write-Host "[3/4] 创建环境变量文件..." -ForegroundColor Yellow
if (-not (Test-Path .env)) {
    Copy-Item env.example .env
    Write-Host "[✓] .env 文件已创建" -ForegroundColor Green
} else {
    Write-Host "[!] .env 文件已存在，跳过创建" -ForegroundColor Yellow
}

# 检查端口占用
Write-Host ""
Write-Host "[4/4] 检查端口占用..." -ForegroundColor Yellow
$port3306 = Get-NetTCPConnection -LocalPort 3306 -ErrorAction SilentlyContinue
$port6379 = Get-NetTCPConnection -LocalPort 6379 -ErrorAction SilentlyContinue

if ($port3306) {
    Write-Host "[!] 警告: 端口 3306 已被占用，可能需要修改 .env 中的 MYSQL_PORT" -ForegroundColor Yellow
}
if ($port6379) {
    Write-Host "[!] 警告: 端口 6379 已被占用，可能需要修改 .env 中的 REDIS_PORT" -ForegroundColor Yellow
}
if (-not $port3306 -and -not $port6379) {
    Write-Host "[✓] 端口检查通过" -ForegroundColor Green
}

# 启动 Docker Compose
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   启动 Docker Compose 服务..." -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

docker compose up -d

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "[错误] 启动失败，请检查日志: docker compose logs" -ForegroundColor Red
    Read-Host "按 Enter 键退出"
    exit 1
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "    配置完成！" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "常用命令:" -ForegroundColor Cyan
Write-Host "  查看服务状态: docker compose ps" -ForegroundColor White
Write-Host "  查看日志:      docker compose logs -f" -ForegroundColor White
Write-Host "  停止服务:      docker compose stop" -ForegroundColor White
Write-Host "  重启服务:      docker compose restart" -ForegroundColor White
Write-Host ""
Write-Host "等待 30-60 秒后，MySQL 将自动初始化数据库..." -ForegroundColor Yellow
Write-Host ""
Read-Host "按 Enter 键退出"
