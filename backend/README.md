# 华康电器连锁积分小程序 - 后端项目

## 项目简介

华康会员积分及商品展示小程序的后端服务，采用Spring Boot 3.2 + JDK 21 + MySQL 8.0 + Redis 7.0技术栈。

## 项目结构

```
backend/
├── pom.xml                          # 父POM文件
├── README.md                        # 项目说明
├── huakang-common/                  # 公共模块
│   └── 统一响应、异常处理、工具类、常量
├── huakang-mapper/                  # 数据访问层
│   └── MyBatis配置、实体类、Mapper接口
├── huakang-service/                 # 业务服务层
│   └── 业务逻辑实现、Redis配置
├── huakang-admin/                   # 后台管理API
│   └── 管理员端、店员端接口（端口8080）
└── huakang-miniapp/                 # 小程序API
    └── 微信小程序接口（端口8081）
```

## 技术栈

- **框架：** Spring Boot 3.2.0
- **JDK：** JDK 21
- **数据库：** MySQL 8.0
- **缓存：** Redis 7.0
- **ORM：** MyBatis-Plus 3.5.5
- **文档：** Knife4j 4.4.0
- **工具：** Lombok, Hutool

## 快速开始

### 1. 环境要求

- JDK 21+
- Maven 3.6+
- MySQL 8.0+
- Redis 7.0+

### 2. 数据库初始化

```bash
# 执行数据库初始化脚本（在项目根目录）
mysql -u root -p < ../database_init_enterprise.sql
mysql -u root -p < ../index_optimization.sql
```

### 3. 启动Redis

```bash
# 使用Docker启动Redis
docker run -d -p 6379:6379 redis:7.0

# 或使用docker-compose
docker compose up -d redis
```

### 4. 编译项目

```bash
# 在backend目录下执行
cd backend
mvn clean install
```

### 5. 启动项目

**启动后台管理API：**
```bash
cd huakang-admin
mvn spring-boot:run
# 或
java -jar target/huakang-admin-1.0.0.jar
```

访问：http://localhost:8080/api/admin  
API文档：http://localhost:8080/api/admin/doc.html

**启动小程序API：**
```bash
cd huakang-miniapp
mvn spring-boot:run
# 或
java -jar target/huakang-miniapp-1.0.0.jar
```

访问：http://localhost:8081/api/miniapp  
API文档：http://localhost:8081/api/miniapp/doc.html

## 配置说明

### 开发环境配置

修改 `application-dev.yml` 中的数据库和Redis连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/hk_electronics
    username: root
    password: root
  data:
    redis:
      host: localhost
      port: 6379
```

### 生产环境配置

使用环境变量配置：

```bash
export DB_HOST=your-db-host
export DB_PORT=3306
export DB_NAME=hk_electronics
export DB_USERNAME=root
export DB_PASSWORD=your-password
export REDIS_HOST=your-redis-host
export REDIS_PORT=6379
export REDIS_PASSWORD=your-redis-password
```

## 模块说明

### huakang-common
公共模块，包含：
- `Result` - 统一响应结果
- `PageResult` - 分页响应结果
- `BusinessException` - 业务异常
- `GlobalExceptionHandler` - 全局异常处理
- 工具类、常量等

### huakang-mapper
数据访问层，包含：
- 实体类（Entity）
- Mapper接口
- MyBatis XML映射文件
- MyBatis-Plus配置

### huakang-service
业务服务层，包含：
- Service接口和实现
- Redis配置
- JWT工具类
- 业务逻辑

### huakang-admin
后台管理API模块，包含：
- 管理员端接口
- 店员端接口
- Spring Security配置
- API文档（Knife4j）

### huakang-miniapp
小程序API模块，包含：
- 微信小程序接口
- API文档（Knife4j）

## 开发规范

### 包命名规范

```
com.huakang
├── common          # 公共模块
├── mapper          # 数据访问层
│   ├── entity      # 实体类
│   ├── mapper      # Mapper接口
│   └── config      # 配置类
├── service         # 业务服务层
│   ├── service     # Service接口
│   ├── impl        # Service实现
│   └── config      # 配置类
├── admin           # 后台管理API
│   ├── controller  # Controller
│   ├── dto         # 数据传输对象
│   └── config      # 配置类
└── miniapp         # 小程序API
    ├── controller  # Controller
    ├── dto         # 数据传输对象
    └── config      # 配置类
```

### 代码规范

- 使用Lombok减少样板代码
- 统一使用Result作为响应结果
- 使用@Valid进行参数校验
- 使用@Transactional保证事务
- 使用统一异常处理

## API文档

启动项目后，访问：
- 后台管理API文档：http://localhost:8080/api/admin/doc.html
- 小程序API文档：http://localhost:8081/api/miniapp/doc.html

## 许可证

Apache 2.0
