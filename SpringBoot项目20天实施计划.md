# 华康电器连锁积分小程序 - Spring Boot 20天实施计划

## 📋 项目概述

**技术栈：** Spring Boot 3.x + JDK 21 + MySQL 8.0 + Redis 7.0  
**项目周期：** 20个工作日（4周）  
**开发模式：** 高效开发，并行任务，快速迭代

---

## 🎯 技术栈确认

### 核心技术
- **框架：** Spring Boot 3.2.x
- **JDK：** JDK 21 (LTS)
- **数据库：** MySQL 8.0
- **缓存：** Redis 7.0
- **ORM：** MyBatis-Plus 3.5.x
- **认证：** Spring Security + JWT
- **文档：** Knife4j (Swagger增强版)
- **工具：** Lombok, MapStruct

### 项目依赖
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.0</version>
</parent>

<properties>
    <java.version>21</java.version>
    <mybatis-plus.version>3.5.5</mybatis-plus.version>
    <knife4j.version>4.4.0</knife4j.version>
</properties>
```

---

## 📅 20天详细计划

### 第1阶段：项目搭建与环境准备（2天）

#### Day 1：项目初始化
**任务清单：**
- [ ] 创建Spring Boot项目（Spring Initializr）
- [ ] 配置Maven依赖（pom.xml）
- [ ] 配置application.yml（开发/生产环境）
- [ ] 配置Docker Compose（MySQL + Redis）
- [ ] 创建项目目录结构
- [ ] 配置Git仓库
- [ ] 配置代码规范（Checkstyle/SpotBugs）

**项目结构：**
```
backend/
├── src/main/java/com/huakang/
│   ├── HuakangApplication.java
│   ├── config/              # 配置类
│   │   ├── MybatisPlusConfig.java
│   │   ├── RedisConfig.java
│   │   ├── SecurityConfig.java
│   │   └── SwaggerConfig.java
│   ├── controller/          # 控制器
│   │   ├── miniapp/         # 小程序API
│   │   └── admin/           # 后台管理API
│   ├── service/             # 业务逻辑
│   │   ├── impl/
│   ├── mapper/              # MyBatis Mapper
│   ├── entity/              # 实体类
│   ├── dto/                 # 数据传输对象
│   ├── vo/                  # 视图对象
│   ├── common/              # 公共类
│   │   ├── Result.java      # 统一响应
│   │   ├── PageResult.java  # 分页响应
│   │   └── constants/       # 常量
│   ├── utils/               # 工具类
│   ├── exception/           # 异常处理
│   └── interceptor/          # 拦截器
├── src/main/resources/
│   ├── mapper/              # MyBatis XML
│   ├── application.yml
│   └── application-dev.yml
└── docker-compose.yml
```

**产出：**
- ✅ 项目框架搭建完成
- ✅ 数据库连接配置完成
- ✅ Redis连接配置完成

#### Day 2：数据库初始化与基础配置
**任务清单：**
- [ ] 执行 `database_init_enterprise.sql` 创建表结构
- [ ] 执行 `index_optimization.sql` 创建索引
- [ ] 创建Entity实体类（6张表）
- [ ] 配置MyBatis-Plus（分页插件、逻辑删除）
- [ ] 创建BaseEntity（公共字段）
- [ ] 配置统一响应Result类
- [ ] 配置全局异常处理
- [ ] 配置日志（Logback）

**实体类创建顺序：**
1. Admin, Staff（基础表）
2. SystemConfig（配置表）
3. Member, Product（业务表）
4. PointsRecord（关联表）

**产出：**
- ✅ 数据库表结构创建完成
- ✅ 实体类创建完成
- ✅ 基础配置完成

---

### 第2阶段：核心功能开发（12天）

#### Day 3-4：认证授权模块（2天）

**Day 3：登录功能**
- [ ] 创建AdminService、StaffService
- [ ] 实现密码加密（BCrypt）
- [ ] 实现管理员登录接口
- [ ] 实现店员登录接口
- [ ] 实现JWT Token生成
- [ ] 实现登录失败锁定机制
- [ ] 创建JWT工具类

**接口：**
```java
POST /api/admin/auth/login        // 管理员登录
POST /api/admin/auth/staff-login  // 店员登录
```

**Day 4：权限控制**
- [ ] 配置Spring Security
- [ ] 创建JWT认证过滤器
- [ ] 实现权限注解（@PreAuthorize）
- [ ] 实现角色区分（Admin/Staff）
- [ ] 实现微信小程序登录（获取OpenID）
- [ ] 创建会员自动注册逻辑

**接口：**
```java
POST /api/miniapp/auth/wechat-login  // 微信登录
```

**产出：**
- ✅ 认证授权模块完成
- ✅ 权限控制完成

---

#### Day 5-7：会员管理模块（3天）

**Day 5：会员基础功能**
- [ ] 创建MemberService、MemberMapper
- [ ] 实现会员列表查询（分页、搜索）
- [ ] 实现会员详情查询
- [ ] 实现创建会员接口
- [ ] 实现会员信息更新

**接口：**
```java
GET  /api/admin/members           // 会员列表
GET  /api/admin/members/{id}      // 会员详情
POST /api/admin/members           // 创建会员
PUT  /api/admin/members/{id}      // 更新会员
```

**Day 6：积分操作核心**
- [ ] 创建PointsRecordService
- [ ] 实现积分调整接口（增/删/改）
- [ ] **关键：实现事务保证数据一致性**
- [ ] 实现余额快照逻辑（balance_before/balance_after）
- [ ] 实现积分记录插入
- [ ] 实现会员积分总额更新

**接口：**
```java
POST /api/admin/members/{id}/points/adjust  // 调整积分
```

**核心代码逻辑：**
```java
@Transactional
public void adjustPoints(Long memberId, Integer points, String changeType, ...) {
    // 1. 查询当前余额
    Member member = memberMapper.selectById(memberId);
    int balanceBefore = member.getTotalPoints();
    
    // 2. 计算新余额
    int balanceAfter = balanceBefore + points;
    
    // 3. 插入积分记录（包含余额快照）
    PointsRecord record = new PointsRecord();
    record.setMemberId(memberId);
    record.setPoints(points);
    record.setBalanceBefore(balanceBefore);
    record.setBalanceAfter(balanceAfter);
    // ... 设置其他字段
    pointsRecordMapper.insert(record);
    
    // 4. 更新会员积分总额
    member.setTotalPoints(balanceAfter);
    memberMapper.updateById(member);
}
```

**Day 7：积分记录查询**
- [ ] 实现会员积分记录查询
- [ ] 实现权限控制（店员只能看自己的操作）
- [ ] 实现筛选功能（会员、操作人、时间范围）
- [ ] 实现小程序端会员信息查询

**接口：**
```java
GET /api/admin/members/{id}/points-records  // 积分记录
GET /api/miniapp/member/info                // 会员信息
GET /api/miniapp/member/points               // 积分总览
GET /api/miniapp/member/points-records       // 积分记录
```

**产出：**
- ✅ 会员管理模块完成
- ✅ 积分操作完成（核心功能）

---

#### Day 8-9：商品管理模块（2天）

**Day 8：商品CRUD**
- [ ] 创建ProductService、ProductMapper
- [ ] 实现商品列表查询（全量商品、积分商品）
- [ ] 实现商品详情查询
- [ ] 实现创建商品接口
- [ ] 实现更新商品接口
- [ ] 实现下架商品接口（软删除）
- [ ] 实现商品图片上传（OSS/本地）

**接口：**
```java
GET    /api/admin/products        // 商品列表
GET    /api/admin/products/{id}   // 商品详情
POST   /api/admin/products        // 创建商品
PUT    /api/admin/products/{id}   // 更新商品
DELETE /api/admin/products/{id}   // 下架商品
POST   /api/admin/products/upload // 图片上传
```

**Day 9：小程序端商品接口**
- [ ] 实现小程序全量商品列表
- [ ] 实现小程序积分商品列表
- [ ] 实现商品详情接口
- [ ] 实现Redis缓存（商品列表缓存5分钟）

**接口：**
```java
GET /api/miniapp/products/list        // 全量商品列表
GET /api/miniapp/products/points-list // 积分商品列表
GET /api/miniapp/products/{id}         // 商品详情
```

**产出：**
- ✅ 商品管理模块完成

---

#### Day 10-11：数据大屏模块（2天）

**Day 10：统计指标**
- [ ] 创建DashboardService
- [ ] 实现会员总数统计
- [ ] 实现累计发放积分统计
- [ ] 实现累计消耗积分统计
- [ ] 实现当前积分池统计
- [ ] 实现Redis缓存（统计数据缓存1分钟）

**接口：**
```java
GET /api/admin/dashboard/stats  // 核心指标
```

**Day 11：趋势图表**
- [ ] 实现近30天新用户趋势
- [ ] 实现近30天积分发放/消耗趋势
- [ ] 优化SQL查询性能
- [ ] 实现数据缓存策略

**接口：**
```java
GET /api/admin/dashboard/trends  // 趋势数据
```

**产出：**
- ✅ 数据大屏模块完成

---

#### Day 12-13：店员管理 + 系统设置（2天）

**Day 12：店员账号管理**
- [ ] 创建StaffService
- [ ] 实现店员列表查询
- [ ] 实现创建店员接口
- [ ] 实现更新店员接口
- [ ] 实现启用/禁用接口
- [ ] 实现重置密码接口

**接口：**
```java
GET    /api/admin/staffs                    // 店员列表
POST   /api/admin/staffs                    // 创建店员
PUT    /api/admin/staffs/{id}               // 更新店员
POST   /api/admin/staffs/{id}/toggle        // 启用/禁用
POST   /api/admin/staffs/{id}/reset-password // 重置密码
```

**Day 13：系统设置**
- [ ] 创建SystemConfigService
- [ ] 实现获取系统配置接口
- [ ] 实现更新系统配置接口
- [ ] 实现企业微信二维码上传
- [ ] 实现小程序端获取二维码接口

**接口：**
```java
GET  /api/admin/system/configs              // 获取配置
POST /api/admin/system/configs             // 更新配置
POST /api/admin/system/wechat-qrcode       // 上传二维码
GET  /api/miniapp/system/wechat-qrcode    // 获取二维码
```

**产出：**
- ✅ 店员管理模块完成
- ✅ 系统设置模块完成

---

#### Day 14：积分对账与优化（1天）

**任务清单：**
- [ ] 创建积分对账Service
- [ ] 实现对账存储过程调用
- [ ] 实现定时任务（每天凌晨对账）
- [ ] 实现对账结果告警（日志/邮件）
- [ ] 优化SQL查询性能
- [ ] 优化Redis缓存策略
- [ ] 代码Review和重构

**定时任务：**
```java
@Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2点
public void checkPointsConsistency() {
    // 调用存储过程检查积分一致性
    // 发现不一致时记录日志/发送告警
}
```

**产出：**
- ✅ 对账功能完成
- ✅ 性能优化完成

---

### 第3阶段：测试与优化（4天）

#### Day 15-16：接口测试（2天）

**Day 15：功能测试**
- [ ] 使用Postman创建测试集合
- [ ] 测试所有接口功能
- [ ] 测试权限控制
- [ ] 测试异常情况
- [ ] 测试边界值
- [ ] 修复发现的Bug

**Day 16：集成测试**
- [ ] 测试积分操作事务
- [ ] 测试并发积分操作
- [ ] 测试数据一致性
- [ ] 测试Redis缓存
- [ ] 测试权限隔离（店员只能看自己的操作）

**产出：**
- ✅ 接口测试完成
- ✅ Bug修复完成

---

#### Day 17：性能测试与优化（1天）

**任务清单：**
- [ ] 使用JMeter进行压力测试
- [ ] 测试接口响应时间（目标：< 500ms）
- [ ] 测试并发性能（目标：100并发）
- [ ] 优化慢查询SQL
- [ ] 优化Redis缓存策略
- [ ] 优化数据库连接池配置
- [ ] 添加接口限流（防止接口被刷）

**性能目标：**
- API响应时间 < 500ms（95分位）
- 支持100并发用户
- 数据库查询 < 100ms

**产出：**
- ✅ 性能测试完成
- ✅ 性能优化完成

---

#### Day 18：安全测试与文档（1天）

**任务清单：**
- [ ] SQL注入测试
- [ ] XSS攻击测试
- [ ] CSRF防护测试
- [ ] Token安全测试
- [ ] 接口权限测试
- [ ] 编写API文档（Knife4j）
- [ ] 编写部署文档
- [ ] 编写运维文档

**产出：**
- ✅ 安全测试完成
- ✅ 文档完成

---

### 第4阶段：部署与上线（2天）

#### Day 19：Docker容器化与部署准备（1天）

**任务清单：**
- [ ] 编写Dockerfile
- [ ] 编写docker-compose.yml（包含应用、MySQL、Redis）
- [ ] 配置生产环境变量
- [ ] 配置Nginx反向代理
- [ ] 配置SSL证书
- [ ] 配置日志收集
- [ ] 配置健康检查
- [ ] 测试容器运行

**Dockerfile示例：**
```dockerfile
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/huakang-backend-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**docker-compose.yml：**
```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - mysql
      - redis
    environment:
      - SPRING_PROFILES_ACTIVE=prod
  
  mysql:
    image: mysql:8.0
    # ... 配置
  
  redis:
    image: redis:7.0
    # ... 配置
```

**产出：**
- ✅ Docker容器化完成
- ✅ 部署脚本完成

---

#### Day 20：生产环境部署与上线（1天）

**任务清单：**
- [ ] 服务器环境准备（购买/配置）
- [ ] 域名配置（如需要）
- [ ] SSL证书配置
- [ ] 生产环境数据库初始化
- [ ] 生产环境部署
- [ ] 功能验证
- [ ] 性能监控配置
- [ ] 日志监控配置
- [ ] 告警配置
- [ ] 数据备份验证
- [ ] 上线检查清单

**上线检查清单：**
- [ ] 所有接口功能正常
- [ ] 权限控制正确
- [ ] 数据一致性检查通过
- [ ] 性能指标达标
- [ ] 监控告警配置完成
- [ ] 日志正常输出
- [ ] 备份策略验证

**产出：**
- ✅ 生产环境部署完成
- ✅ 系统上线完成

---

## 📊 时间分配表

| 阶段 | 任务 | 天数 | 累计 |
|------|------|------|------|
| 第1阶段 | 项目搭建与环境准备 | 2天 | 2天 |
| 第2阶段 | 核心功能开发 | 12天 | 14天 |
| 第3阶段 | 测试与优化 | 4天 | 18天 |
| 第4阶段 | 部署与上线 | 2天 | 20天 |

**总计：20个工作日**

---

## 🚀 高效开发技巧

### 1. 并行开发策略

**可以并行开发的任务：**
- 认证授权 + 数据库初始化
- 会员管理 + 商品管理（不同开发者）
- 数据大屏 + 店员管理
- 接口测试 + 性能优化

### 2. 代码生成工具

**使用MyBatis-Plus代码生成器：**
```java
// 快速生成Entity、Mapper、Service、Controller
FastAutoGenerator.create(...)
    .globalConfig(...)
    .packageConfig(...)
    .strategyConfig(...)
    .execute();
```

### 3. 模板代码

**创建Controller模板：**
```java
@RestController
@RequestMapping("/api/admin/members")
@Api(tags = "会员管理")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    
    // 使用Lombok @RequiredArgsConstructor自动注入
}
```

### 4. 快速开发清单

**每天开始前：**
- [ ] 拉取最新代码
- [ ] 确认今日任务
- [ ] 创建功能分支

**每天结束前：**
- [ ] 提交代码（Commit Message规范）
- [ ] 更新任务进度
- [ ] 记录遇到的问题

---

## 📝 关键技术实现

### 1. 统一响应Result

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {
    private Integer code;
    private String message;
    private T data;
    
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }
}
```

### 2. 分页查询

```java
// Controller
@GetMapping
public Result<PageResult<MemberVO>> list(
    @RequestParam(defaultValue = "1") Integer page,
    @RequestParam(defaultValue = "10") Integer size,
    String keyword
) {
    Page<Member> pageParam = new Page<>(page, size);
    return Result.success(memberService.list(pageParam, keyword));
}

// Service
public PageResult<MemberVO> list(Page<Member> page, String keyword) {
    LambdaQueryWrapper<Member> wrapper = new LambdaQueryWrapper<>();
    wrapper.like(StringUtils.hasText(keyword), Member::getNickname, keyword)
           .or(StringUtils.hasText(keyword), w -> w.like(Member::getPhone, keyword))
           .eq(Member::getIsDeleted, 0)
           .orderByDesc(Member::getCreatedAt);
    Page<Member> result = memberMapper.selectPage(page, wrapper);
    return PageResult.of(result, this::toVO);
}
```

### 3. JWT认证

```java
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final String secret = "your-secret-key";
    private final long validityInMilliseconds = 3600000; // 1小时
    
    public String generateToken(String username, String role) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("role", role);
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);
        
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(SignatureAlgorithm.HS256, secret)
            .compact();
    }
}
```

### 4. Redis缓存

```java
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductMapper productMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    
    @Cacheable(value = "products", key = "'list:' + #type + ':' + #page + ':' + #size")
    public PageResult<ProductVO> list(Integer type, Integer page, Integer size) {
        // 查询逻辑
    }
    
    @CacheEvict(value = "products", allEntries = true)
    public void create(ProductDTO dto) {
        // 创建逻辑，清除缓存
    }
}
```

### 5. 事务管理

```java
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class PointsService {
    private final MemberMapper memberMapper;
    private final PointsRecordMapper pointsRecordMapper;
    
    public void adjustPoints(Long memberId, Integer points, String changeType, ...) {
        // 1. 查询会员（加锁）
        Member member = memberMapper.selectByIdForUpdate(memberId);
        
        // 2. 计算余额
        int balanceBefore = member.getTotalPoints();
        int balanceAfter = balanceBefore + points;
        
        // 3. 插入记录
        PointsRecord record = new PointsRecord();
        // ... 设置字段
        pointsRecordMapper.insert(record);
        
        // 4. 更新会员积分
        member.setTotalPoints(balanceAfter);
        memberMapper.updateById(member);
    }
}
```

---

## ⚠️ 风险与应对

### 风险1：积分数据一致性
**应对：** 
- 使用 `@Transactional` 保证事务
- 使用 `SELECT ... FOR UPDATE` 加锁
- 定时对账检查

### 风险2：性能瓶颈
**应对：**
- Redis缓存热点数据
- 数据库索引优化
- 连接池配置优化

### 风险3：开发进度延期
**应对：**
- 每日进度检查
- 优先完成核心功能
- 非核心功能可以后续迭代

---

## ✅ 每日检查点

### 每日站会（10分钟）
1. 昨天完成了什么？
2. 今天计划做什么？
3. 遇到什么阻碍？

### 每日结束检查
- [ ] 代码已提交
- [ ] 功能测试通过
- [ ] 无严重Bug
- [ ] 进度正常

---

## 📚 参考资源

### Spring Boot官方文档
- https://spring.io/projects/spring-boot

### MyBatis-Plus文档
- https://baomidou.com/

### Redis文档
- https://redis.io/docs/

### 项目模板
- Spring Boot + MyBatis-Plus + Redis 完整示例

---

## 🎯 成功标准

### 功能完整性
- ✅ 所有需求文档中的功能已实现
- ✅ 所有接口测试通过
- ✅ 权限控制正确

### 性能标准
- ✅ API响应时间 < 500ms
- ✅ 支持100并发用户
- ✅ 数据库查询 < 100ms

### 质量标准
- ✅ 无严重Bug
- ✅ 安全测试通过
- ✅ 文档完整

---

**项目启动日期：** ___________  
**预计完成日期：** ___________  
**项目负责人：** ___________

---

*本文档将根据项目进展持续更新*
