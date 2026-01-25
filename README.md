# 华康电器连锁积分小程序 - 后端系统

## 📋 项目简介

华康会员积分及商品展示小程序后端系统，基于Spring Boot 3.2.0 + JDK 21开发的多模块Maven项目。

## 🛠️ 技术栈

- **框架**: Spring Boot 3.2.0
- **JDK**: 21
- **数据库**: MySQL 8.0
- **缓存**: Redis 7.0
- **ORM**: MyBatis-Plus 3.5.5
- **API文档**: Knife4j 4.4.0
- **工具类**: Hutool 5.8.23
- **JWT**: 0.12.3

## 📁 项目结构

```
backend/
├── huakang-common/      # 公共模块（统一响应、异常处理、工具类）
├── huakang-mapper/       # 数据访问层（MyBatis配置、实体类、Mapper接口）
├── huakang-service/      # 业务服务层（业务逻辑实现）
├── huakang-admin/        # 后台管理API（管理员端、店员端接口）
└── huakang-miniapp/      # 小程序API（微信小程序接口）
```

## 🚀 快速开始

### 环境要求

- JDK 21+
- Maven 3.6+
- MySQL 8.0+
- Redis 7.0+
- Docker（可选，用于快速启动MySQL和Redis）

### 启动步骤

#### 1. 克隆项目

```bash
git clone https://github.com/YOUR_USERNAME/huakang-electronics-backend.git
cd huakang-electronics-backend
```

#### 2. 启动MySQL和Redis（使用Docker）

```bash
docker-compose up -d
```

或手动启动MySQL和Redis服务。

#### 3. 导入数据库

```bash
mysql -u root -p < database_init_enterprise.sql
mysql -u root -p < index_optimization.sql
```

#### 4. 修改配置文件

编辑 `backend/huakang-admin/src/main/resources/application.yml` 和 `backend/huakang-miniapp/src/main/resources/application.yml`，修改数据库连接信息。

#### 5. 编译项目

```bash
cd backend
mvn clean install
```

#### 6. 启动后台管理API

```bash
cd huakang-admin
mvn spring-boot:run
```

访问：http://localhost:8080/api/admin

#### 7. 启动小程序API（新终端）

```bash
cd huakang-miniapp
mvn spring-boot:run
```

访问：http://localhost:8081/api/miniapp

## 📚 API文档

启动项目后访问：

- **后台管理API文档**: http://localhost:8080/api/admin/doc.html
- **小程序API文档**: http://localhost:8081/api/miniapp/doc.html

## 🎯 核心功能

### 已实现功能

- ✅ 多模块Spring Boot项目架构
- ✅ 统一响应结果封装
- ✅ 全局异常处理
- ✅ MyBatis-Plus集成
- ✅ Redis缓存配置
- ✅ Knife4j API文档
- ✅ 导出功能（积分记录、消费记录、财务报表）
- ✅ 上门服务订单预留接口

### 待开发功能

- [ ] 会员管理
- [ ] 商品管理
- [ ] 积分管理
- [ ] 订单管理
- [ ] 店员管理
- [ ] 管理员管理
- [ ] 系统配置管理

## 📖 项目文档

- [数据库设计文档](./数据库设计文档.md)
- [企业级数据库设计说明](./企业级数据库设计说明.md)
- [系统架构设计文档](./系统架构设计文档.md)
- [SpringBoot项目20天实施计划](./SpringBoot项目20天实施计划.md)
- [导出功能使用说明](./导出功能使用说明.md)
- [上门服务功能预留接口说明](./上门服务功能预留接口说明.md)
- [GitHub上传指南](./GitHub上传指南.md)

## 🔧 开发规范

### 代码结构

- **Controller层**: 处理HTTP请求，参数校验
- **Service层**: 业务逻辑实现
- **Mapper层**: 数据访问
- **DTO**: 数据传输对象
- **VO**: 视图对象

### 命名规范

- 类名：大驼峰（PascalCase）
- 方法名：小驼峰（camelCase）
- 常量：大写下划线（UPPER_SNAKE_CASE）
- 包名：小写（lowercase）

## 📝 开发计划

参考 [SpringBoot项目20天实施计划](./SpringBoot项目20天实施计划.md) 和 [20天每日任务清单](./20天每日任务清单.md)

## 🤝 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📄 许可证

MIT License

## 👥 作者

huakang

## 🙏 致谢

感谢所有为本项目做出贡献的开发者！

---

**如有问题，请提交Issue或联系开发团队。**
