# 使用多阶段构建优化镜像大小
# 阶段1: 构建阶段
FROM maven:3.9-eclipse-temurin-21 AS builder

# 设置工作目录
WORKDIR /app

# 复制父POM和所有模块的POM文件（利用Docker缓存）
COPY pom.xml .
COPY huakang-common/pom.xml huakang-common/
COPY huakang-mapper/pom.xml huakang-mapper/
COPY huakang-service/pom.xml huakang-service/
COPY huakang-miniapp/pom.xml huakang-miniapp/

# 下载依赖（利用Docker缓存层）
RUN mvn dependency:go-offline -B

# 复制源代码
COPY huakang-common/src huakang-common/src
COPY huakang-mapper/src huakang-mapper/src
COPY huakang-service/src huakang-service/src
COPY huakang-miniapp/src huakang-miniapp/src

# 构建项目（跳过测试）
RUN mvn clean package -DskipTests -pl huakang-miniapp -am

# 阶段2: 运行阶段
FROM eclipse-temurin:21-jre-alpine

# 设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 创建应用目录
WORKDIR /app

# 创建日志目录
RUN mkdir -p /app/logs

# 从构建阶段复制JAR文件
COPY --from=builder /app/huakang-miniapp/target/*.jar app.jar

# 暴露端口
EXPOSE 8081

# 设置JVM参数
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8081/api/miniapp/health/ping || exit 1

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --spring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod}"]
