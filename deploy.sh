#!/bin/bash

# 华康电器连锁积分小程序 - 一键部署脚本
# 使用方法：./deploy.sh [dev|test|prod]

set -e

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 环境参数
ENV=${1:-prod}
COMPOSE_FILE="docker-compose.${ENV}.yml"

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}华康电器连锁积分小程序 - 部署脚本${NC}"
echo -e "${GREEN}环境: ${ENV}${NC}"
echo -e "${GREEN}========================================${NC}"

# 检查环境变量文件
if [ ! -f ".env.${ENV}" ]; then
    echo -e "${RED}错误: 环境变量文件 .env.${ENV} 不存在${NC}"
    echo -e "${YELLOW}请复制 .env.${ENV}.example 并填写配置${NC}"
    exit 1
fi

# 检查docker-compose文件
if [ ! -f "$COMPOSE_FILE" ]; then
    echo -e "${RED}错误: Docker Compose配置文件 ${COMPOSE_FILE} 不存在${NC}"
    exit 1
fi

# 加载环境变量
echo -e "${YELLOW}加载环境变量...${NC}"
export $(cat .env.${ENV} | grep -v '^#' | xargs)

# 检查必需的环境变量
REQUIRED_VARS=("DB_PASSWORD" "REDIS_PASSWORD" "JWT_SECRET")
for var in "${REQUIRED_VARS[@]}"; do
    if [ -z "${!var}" ]; then
        echo -e "${RED}错误: 环境变量 ${var} 未设置${NC}"
        exit 1
    fi
done

echo -e "${GREEN}✓ 环境变量检查通过${NC}"

# 停止旧容器
echo -e "${YELLOW}停止旧容器...${NC}"
docker-compose -f $COMPOSE_FILE down

# 构建镜像
echo -e "${YELLOW}构建Docker镜像...${NC}"
docker-compose -f $COMPOSE_FILE build --no-cache

# 启动服务
echo -e "${YELLOW}启动服务...${NC}"
docker-compose -f $COMPOSE_FILE up -d

# 等待服务启动
echo -e "${YELLOW}等待服务启动...${NC}"
sleep 10

# 检查服务状态
echo -e "${YELLOW}检查服务状态...${NC}"
docker-compose -f $COMPOSE_FILE ps

# 健康检查
echo -e "${YELLOW}执行健康检查...${NC}"
sleep 30

# 检查Admin服务
if curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo -e "${GREEN}✓ Admin服务启动成功${NC}"
else
    echo -e "${RED}✗ Admin服务启动失败${NC}"
    docker-compose -f $COMPOSE_FILE logs admin
    exit 1
fi

# 检查Miniapp服务
if curl -f http://localhost:8081/actuator/health > /dev/null 2>&1; then
    echo -e "${GREEN}✓ Miniapp服务启动成功${NC}"
else
    echo -e "${RED}✗ Miniapp服务启动失败${NC}"
    docker-compose -f $COMPOSE_FILE logs miniapp
    exit 1
fi

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}部署完成！${NC}"
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Admin服务: http://localhost:8080${NC}"
echo -e "${GREEN}Miniapp服务: http://localhost:8081${NC}"
echo -e "${YELLOW}查看日志: docker-compose -f ${COMPOSE_FILE} logs -f${NC}"
echo -e "${YELLOW}停止服务: docker-compose -f ${COMPOSE_FILE} down${NC}"
echo -e "${GREEN}========================================${NC}"
