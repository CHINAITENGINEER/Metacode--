#!/bin/sh

# 华康电器连锁积分小程序 - Docker启动脚本

set -e

# JVM参数配置
JVM_OPTS="${JVM_OPTS:--Xms512m -Xmx1024m}"
JAVA_OPTS="-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod} \
           -Dfile.encoding=UTF-8 \
           -Duser.timezone=Asia/Shanghai \
           -Djava.security.egd=file:/dev/./urandom"

# 启动Admin服务
if [ "$SERVICE_TYPE" = "admin" ]; then
    echo "=========================================="
    echo "Starting Huakang Admin Service..."
    echo "Profile: ${SPRING_PROFILES_ACTIVE:-prod}"
    echo "JVM Options: $JVM_OPTS"
    echo "=========================================="
    exec java $JVM_OPTS $JAVA_OPTS -jar app-admin.jar
fi

# 启动Miniapp服务
if [ "$SERVICE_TYPE" = "miniapp" ]; then
    echo "=========================================="
    echo "Starting Huakang Miniapp Service..."
    echo "Profile: ${SPRING_PROFILES_ACTIVE:-prod}"
    echo "JVM Options: $JVM_OPTS"
    echo "=========================================="
    exec java $JVM_OPTS $JAVA_OPTS -jar app-miniapp.jar
fi

echo "Error: SERVICE_TYPE environment variable not set or invalid"
echo "Please set SERVICE_TYPE to 'admin' or 'miniapp'"
exit 1
