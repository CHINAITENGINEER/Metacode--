# AGENTS.md - 华康电器连锁积分小程序后端项目

## 项目概述

华康电器连锁积分小程序后端服务是一个基于Spring Boot 3.1.5 + JDK 21 + MySQL 8.0 + Redis 7.0技术栈的企业级积分管理与商品展示系统。该项目采用模块化架构设计，支持管理员后台管理和微信小程序端服务，主要功能包括会员积分管理、商品展示、订单管理、积分兑换、数据导出和积分对账等功能。

## 项目架构

### 模块结构

1. **huakang-common** - 公共模块
   - 统一响应结果（Result、ResultCode、PageResult）
   - 全局异常处理（GlobalExceptionHandler）
   - 通用工具类和常量

2. **huakang-mapper** - 数据访问层
   - MyBatis-Plus配置
   - 实体类（Entity）
   - Mapper接口及XML映射文件
   - 自动填充处理器（MyMetaObjectHandler）
   - 分页插件配置

3. **huakang-service** - 业务服务层
   - Service接口与实现类
   - Redis配置和工具类
   - JWT工具类和认证工具
   - 业务逻辑实现
   - 导出服务（EasyExcel）
   - 积分对账服务
   - 定时任务调度

4. **huakang-admin** - 后台管理API模块
   - 管理员端接口
   - 店员端接口
   - Spring Security配置
   - JWT认证过滤器
   - API文档（Knife4j）
   - 定时任务支持

5. **huakang-miniapp** - 小程序API模块
   - 微信小程序接口
   - 小程序API文档（Knife4j）

### 技术栈

- **框架：** Spring Boot 3.1.5
- **JDK：** JDK 21
- **数据库：** MySQL 8.0
- **缓存：** Redis 7.0
- **ORM：** MyBatis-Plus 3.5.8
- **文档：** Knife4j 4.4.0
- **导出：** EasyExcel 3.3.2
- **工具：** Lombok, Hutool, JWT

## 安全与认证

### JWT身份验证
- 使用JWT进行无状态身份验证
- Token有效期2小时（可配置）
- 支持管理员和店员角色区分
- 包含登录失败锁定机制（5次失败后锁定30分钟）
- 支持IP地址追踪和记录

### Spring Security配置
- 禁用CSRF（使用JWT，无需CSRF保护）
- 无状态会话管理（JWT）
- JWT认证过滤器在UsernamePasswordAuthenticationFilter之前执行
- API文档路径、测试接口、认证接口无需认证
- 其他所有请求需要认证
- 支持基于角色的访问控制（RBAC）

### 认证流程
- 管理员登录：/auth/login
- 店员登录：/auth/staff-login
- 使用Bearer Token进行后续认证

## 数据模型

### 主要实体类
- **Admin** - 管理员实体（包含登录失败计数、锁定时间等安全相关字段）
- **Member** - 会员实体（积分管理相关，包含微信OpenID、昵称、头像、手机号、积分总额等）
- **PointsRecord** - 积分记录实体（积分变动记录，包含变动类型、分值、业务单号、操作人等信息）
- **Product** - 商品实体
- **Staff** - 店员实体
- **SystemConfig** - 系统配置实体

### 数据访问特性
- 使用MyBatis-Plus进行数据访问
- 支持逻辑删除（is_deleted字段）
- 自动填充创建时间、更新时间（MyMetaObjectHandler）
- 驼峰命名转换
- 分页查询支持
- 支持批量操作

## 业务功能

### 会员管理
- 会员信息管理
- 积分查询与兑换
- 会员等级管理
- 微信用户同步

### 积分管理
- 积分记录查询
- 积分调整与充值
- 积分对账功能
- 积分流水记录
- 积分变动类型管理（消费赠送、积分兑换、后台调整、新会员注册、积分扣除、积分过期等）

### 商品管理
- 商品信息管理
- 商品分类管理
- 库存管理
- 商品上下架管理

### 订单管理
- 服务订单管理
- 订单状态跟踪
- 订单统计
- 积分兑换订单管理

### 系统管理
- 管理员与店员管理
- 系统配置管理
- 数据导出功能
- 仪表盘统计
- 积分对账管理

## 配置管理

### 多环境配置
- 开发环境 (dev) - 端口8080/8083
- 测试环境 (test) - 端口通过环境变量配置
- 生产环境 (prod) - 端口通过环境变量配置

### 配置项
- 数据库连接（MySQL，支持多环境配置）
- Redis连接（支持多环境配置）
- JWT配置（密钥、过期时间）
- 文件上传限制（最大文件大小、请求大小）
- 日志配置（控制台和文件输出）
- 连接池配置（HikariCP）
- API文档配置（Knife4j）

## 开发规范

### 包命名规范
```
com.huakang
├── common          # 公共模块
│   ├── core        # 核心类
│   └── exception   # 异常处理
├── mapper          # 数据访问层
│   ├── entity      # 实体类
│   ├── mapper      # Mapper接口
│   └── config      # 配置类
├── service         # 业务服务层
│   ├── service     # Service接口
│   ├── impl        # Service实现
│   ├── config      # 配置类
│   ├── dto         # 数据传输对象
│   ├── utils       # 工具类
│   └── schedule    # 定时任务
├── admin           # 后台管理API
│   ├── controller  # Controller
│   ├── dto         # 数据传输对象
│   ├── filter      # 过滤器
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
- 使用@RequiredArgsConstructor进行依赖注入
- 使用日志记录（SLF4J）
- 使用DTO进行数据传输

## API文档

项目使用Knife4j生成API文档：
- 后台管理API文档：http://localhost:8080/api/admin/doc.html
- 小程序API文档：http://localhost:8083/api/miniapp/doc.html

## 统一响应格式

```java
{
  "code": 200,        // 响应码
  "message": "操作成功", // 响应消息
  "data": {}          // 响应数据
}
```

### 常用响应码
- 200: 操作成功
- 400: 参数错误
- 401: 未授权，请先登录
- 403: 无权限访问
- 404: 资源不存在
- 500: 操作失败
- 1001: 用户名或密码错误
- 1002: Token已过期
- 2001: 积分不足
- 2002: 积分操作失败

## 构建与运行

### 环境要求
- JDK 21+
- Maven 3.6+
- MySQL 8.0+
- Redis 7.0+

### 构建命令
```bash
# 编译项目
mvn clean install

# 启动后台管理API
cd huakang-admin
mvn spring-boot:run

# 启动小程序API
cd huakang-miniapp
mvn spring-boot:run
```

### 数据库初始化
```bash
# 执行数据库初始化脚本
mysql -u root -p < ../database_init_enterprise.sql
mysql -u root -p < ../index_optimization.sql
```

### Redis启动
```bash
# 使用Docker启动Redis
docker run -d -p 6379:6379 redis:7.0
```

## 特殊功能

### 积分对账功能
- 定时对账任务（支持手动和自动执行）
- 积分流水对比（计算积分总额与实际值的差异）
- 异常数据处理（发现并标记积分不一致的会员）
- 一致性检查（验证会员积分与积分记录的一致性）

### 数据导出功能
- 消费记录导出（基于积分记录的消费数据）
- 积分记录导出（完整的积分变动历史）
- 财务报告导出（积分发放、消耗、兑换统计）
- Excel格式导出（使用EasyExcel实现）

### 安全措施
- 密码使用BCrypt加密
- 登录失败次数限制
- 会话管理（JWT Token）
- 参数校验（Spring Validation）
- IP地址记录（登录时记录客户端IP）
- SQL注入防护（MyBatis-Plus内置）
- 接口访问控制（Spring Security）

### 高级特性
- 定时任务调度（支持积分对账等定时任务）
- Redis缓存（提升系统性能）
- 统一日志记录（支持控制台和文件输出）
- 多环境配置（开发、测试、生产环境）
- 自动时间字段填充（创建时间和更新时间）
- 逻辑删除（数据安全删除）
- 分页查询（MyBatis-Plus分页插件）