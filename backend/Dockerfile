# 多阶段构建 - 华康电器连锁积分小程序后端
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# 复制pom文件
COPY pom.xml .
COPY huakang-common/pom.xml huakang-common/
COPY huakang-mapper/pom.xml huakang-mapper/
COPY huakang-service/pom.xml huakang-service/
COPY huakang-admin/pom.xml huakang-admin/
COPY huakang-miniapp/pom.xml huakang-miniapp/

# 下载依赖（利用Docker缓存）
RUN mvn dependency:go-offline -B

# 复制源代码
COPY huakang-common huakang-common/
COPY huakang-mapper huakang-mapper/
COPY huakang-service huakang-service/
COPY huakang-admin huakang-admin/
COPY huakang-miniapp huakang-miniapp/

# 编译打包
RUN mvn clean package -DskipTests -B

# 运行阶段
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# 安装时区数据和字体（支持中文）
RUN apk add --no-cache tzdata fontconfig ttf-dejavu && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone && \
    apk del tzdata

# 复制jar包
COPY --from=builder /app/huakang-admin/target/huakang-admin-*.jar app-admin.jar
COPY --from=builder /app/huakang-miniapp/target/huakang-miniapp-*.jar app-miniapp.jar

# 创建日志目录
RUN mkdir -p /app/logs && \
    addgroup -g 1000 huakang && \
    adduser -D -u 1000 -G huakang huakang && \
    chown -R huakang:huakang /app

# 切换到非root用户
USER huakang

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:${SERVER_PORT:-8080}/actuator/health || exit 1

# 暴露端口
EXPOSE 8080 8081

# 启动脚本
COPY docker-entrypoint.sh /
USER root
RUN chmod +x /docker-entrypoint.sh
USER huakang

ENTRYPOINT ["/docker-entrypoint.sh"]
