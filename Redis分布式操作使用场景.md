# Redis分布式操作使用场景分析

## 📋 项目概述

本项目是**单机部署**还是**分布式部署**，决定了是否需要使用Redis的分布式操作。

---

## 🎯 需要使用Redis分布式操作的场景

### 场景1：积分操作并发控制 ⚠️ **最重要**

#### 问题描述
当多个请求同时操作同一个会员的积分时，可能出现数据不一致：
```
时间线：
T1: 请求A查询会员积分 = 1000
T2: 请求B查询会员积分 = 1000
T3: 请求A增加500积分，更新为1500
T4: 请求B扣除300积分，更新为700（错误！应该是1200）
```

#### 解决方案：分布式锁
使用Redis分布式锁保证同一时间只有一个请求能操作会员积分。

**实现方式：**
```java
@Service
@RequiredArgsConstructor
public class PointsService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    
    /**
     * 调整积分（使用分布式锁）
     */
    public void adjustPoints(Long memberId, Integer points, String changeType, ...) {
        String lockKey = "lock:member:points:" + memberId;
        String lockValue = UUID.randomUUID().toString();
        
        try {
            // 1. 获取分布式锁（30秒超时）
            Boolean lockAcquired = stringRedisTemplate.opsForValue()
                .setIfAbsent(lockKey, lockValue, Duration.ofSeconds(30));
            
            if (!lockAcquired) {
                throw new BusinessException("积分操作中，请稍后重试");
            }
            
            // 2. 执行积分操作（在锁保护下）
            doAdjustPoints(memberId, points, changeType, ...);
            
        } finally {
            // 3. 释放锁（使用Lua脚本保证原子性）
            releaseLock(lockKey, lockValue);
        }
    }
    
    /**
     * 释放锁（Lua脚本保证原子性）
     */
    private void releaseLock(String lockKey, String lockValue) {
        String luaScript = 
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "    return redis.call('del', KEYS[1]) " +
            "else " +
            "    return 0 " +
            "end";
        
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(luaScript);
        script.setResultType(Long.class);
        
        stringRedisTemplate.execute(script, 
            Collections.singletonList(lockKey), 
            lockValue);
    }
}
```

**使用场景：**
- ✅ **必须使用**：如果系统部署了多个实例（负载均衡）
- ✅ **建议使用**：即使单机部署，也要考虑未来扩展
- ⚠️ **高并发场景**：多个店员同时操作同一个会员的积分

---

### 场景2：分布式Session（多实例部署）

#### 问题描述
如果系统部署了多个实例，用户登录后访问不同实例，Session无法共享。

#### 解决方案：Redis存储Session
使用Spring Session + Redis实现分布式Session。

**配置方式：**
```java
// pom.xml
<dependency>
    <groupId>org.springframework.session</groupId>
    <artifactId>spring-session-data-redis</artifactId>
</dependency>

// application.yml
spring:
  session:
    store-type: redis
    timeout: 3600  # 1小时
  redis:
    host: localhost
    port: 6379
```

**使用场景：**
- ✅ **必须使用**：如果系统部署了多个实例（负载均衡）
- ❌ **不需要**：单机部署不需要

---

### 场景3：分布式限流（防止接口被刷）

#### 问题描述
防止恶意请求刷接口，如登录接口、积分调整接口。

#### 解决方案：Redis分布式限流
使用Redis实现分布式限流（滑动窗口或令牌桶算法）。

**实现方式：**
```java
@Component
@RequiredArgsConstructor
public class RateLimiter {
    private final StringRedisTemplate stringRedisTemplate;
    
    /**
     * 限流检查（滑动窗口算法）
     * @param key 限流key（如：用户ID、IP地址）
     * @param limit 限制次数
     * @param windowSeconds 时间窗口（秒）
     */
    public boolean tryAcquire(String key, int limit, int windowSeconds) {
        String redisKey = "rate_limit:" + key;
        long currentTime = System.currentTimeMillis();
        long windowStart = currentTime - windowSeconds * 1000L;
        
        // 1. 移除过期记录
        stringRedisTemplate.opsForZSet().removeRangeByScore(
            redisKey, 0, windowStart);
        
        // 2. 统计当前窗口内的请求数
        Long count = stringRedisTemplate.opsForZSet()
            .count(redisKey, windowStart, currentTime);
        
        if (count != null && count >= limit) {
            return false; // 超过限制
        }
        
        // 3. 记录当前请求
        stringRedisTemplate.opsForZSet().add(
            redisKey, 
            UUID.randomUUID().toString(), 
            currentTime);
        
        // 4. 设置过期时间
        stringRedisTemplate.expire(redisKey, 
            Duration.ofSeconds(windowSeconds + 1));
        
        return true;
    }
}

// 使用示例
@RestController
public class AuthController {
    private final RateLimiter rateLimiter;
    
    @PostMapping("/api/admin/auth/login")
    public Result login(@RequestBody LoginDTO dto, HttpServletRequest request) {
        String ip = getClientIp(request);
        
        // 限流：每个IP每分钟最多5次登录
        if (!rateLimiter.tryAcquire("login:" + ip, 5, 60)) {
            throw new BusinessException("登录过于频繁，请稍后重试");
        }
        
        // 登录逻辑...
    }
}
```

**使用场景：**
- ✅ **建议使用**：所有对外接口都应该有限流保护
- ⚠️ **必须使用**：登录接口、积分调整接口等敏感接口

---

### 场景4：分布式计数器（统计功能）

#### 问题描述
数据大屏的统计数据需要实时更新，如果多实例部署，需要分布式计数器。

#### 解决方案：Redis分布式计数器
使用Redis的INCR命令实现分布式计数器。

**实现方式：**
```java
@Service
@RequiredArgsConstructor
public class DashboardService {
    private final StringRedisTemplate stringRedisTemplate;
    
    /**
     * 获取会员总数（带缓存）
     */
    public Long getTotalMembers() {
        String cacheKey = "stats:total_members";
        String cached = stringRedisTemplate.opsForValue().get(cacheKey);
        
        if (cached != null) {
            return Long.parseLong(cached);
        }
        
        // 从数据库查询
        Long count = memberMapper.selectCount(
            new LambdaQueryWrapper<Member>()
                .eq(Member::getIsDeleted, 0));
        
        // 缓存1分钟
        stringRedisTemplate.opsForValue().set(
            cacheKey, 
            String.valueOf(count), 
            Duration.ofMinutes(1));
        
        return count;
    }
    
    /**
     * 增加会员计数（实时更新）
     */
    public void incrementMemberCount() {
        String cacheKey = "stats:total_members";
        stringRedisTemplate.opsForValue().increment(cacheKey);
        stringRedisTemplate.expire(cacheKey, Duration.ofMinutes(1));
    }
}
```

**使用场景：**
- ✅ **建议使用**：如果多实例部署，统计数据需要实时更新
- ❌ **不需要**：单机部署，直接查数据库即可

---

### 场景5：分布式消息队列（异步任务）

#### 问题描述
某些耗时操作（如发送邮件、生成报表）需要异步处理。

#### 解决方案：Redis List作为消息队列
使用Redis List实现简单的消息队列。

**实现方式：**
```java
@Service
@RequiredArgsConstructor
public class AsyncTaskService {
    private final StringRedisTemplate stringRedisTemplate;
    
    /**
     * 发送任务到队列
     */
    public void sendTask(String taskType, Object data) {
        String queueKey = "queue:task:" + taskType;
        stringRedisTemplate.opsForList().rightPush(
            queueKey, 
            JSON.toJSONString(data));
    }
    
    /**
     * 消费任务（定时任务调用）
     */
    @Scheduled(fixedDelay = 5000) // 每5秒执行一次
    public void consumeTasks() {
        String queueKey = "queue:task:email";
        String task = stringRedisTemplate.opsForList().leftPop(queueKey);
        
        if (task != null) {
            // 处理任务
            processTask(task);
        }
    }
}
```

**使用场景：**
- ✅ **可选**：如果有异步任务需求
- ⚠️ **建议**：使用专业的消息队列（RabbitMQ、Kafka）更合适

---

## 📊 使用场景总结表

| 场景 | 单机部署 | 多实例部署 | 优先级 |
|------|---------|-----------|--------|
| **分布式锁（积分操作）** | 可选 | **必须** | ⚠️ 高 |
| **分布式Session** | 不需要 | **必须** | ⚠️ 高 |
| **分布式限流** | 建议 | **必须** | ⚠️ 高 |
| **分布式计数器** | 可选 | 建议 | 中 |
| **分布式消息队列** | 可选 | 可选 | 低 |

---

## 🎯 推荐方案

### 方案一：单机部署（初期）

**需要的Redis功能：**
1. ✅ **缓存**：商品列表、统计数据（必须）
2. ✅ **分布式限流**：防止接口被刷（建议）
3. ❌ **分布式锁**：不需要（单机事务即可）
4. ❌ **分布式Session**：不需要

**Redis使用场景：**
```java
// 1. 缓存商品列表
@Cacheable(value = "products", key = "'list:' + #type")
public List<ProductVO> listProducts(Integer type) { ... }

// 2. 限流保护
if (!rateLimiter.tryAcquire("login:" + ip, 5, 60)) {
    throw new BusinessException("登录过于频繁");
}
```

---

### 方案二：多实例部署（生产环境）

**需要的Redis功能：**
1. ✅ **缓存**：商品列表、统计数据（必须）
2. ✅ **分布式锁**：积分操作并发控制（必须）
3. ✅ **分布式Session**：Session共享（必须）
4. ✅ **分布式限流**：防止接口被刷（必须）
5. ✅ **分布式计数器**：实时统计（建议）

**Redis使用场景：**
```java
// 1. 分布式锁（积分操作）
String lockKey = "lock:member:points:" + memberId;
Boolean lock = redisTemplate.opsForValue()
    .setIfAbsent(lockKey, lockValue, Duration.ofSeconds(30));
try {
    // 执行积分操作
} finally {
    releaseLock(lockKey, lockValue);
}

// 2. 分布式Session（Spring Session自动处理）
// 3. 分布式限流（同上）
// 4. 缓存（同上）
```

---

## ⚠️ 关键注意事项

### 1. 分布式锁的注意事项

**问题1：锁超时**
- 如果业务执行时间超过锁的超时时间，锁会自动释放
- **解决**：设置合理的超时时间，或使用看门狗机制

**问题2：锁误释放**
- 如果线程A的锁被线程B释放，会导致问题
- **解决**：使用Lua脚本保证原子性（已在代码中实现）

**问题3：死锁**
- 如果获取锁后程序崩溃，锁无法释放
- **解决**：设置锁的过期时间（已在代码中实现）

### 2. 分布式Session的注意事项

**问题：Session过期**
- Redis中的Session过期后，用户需要重新登录
- **解决**：设置合理的过期时间，或实现自动续期

### 3. 分布式限流的注意事项

**问题：限流精度**
- 滑动窗口算法在分布式环境下可能有精度问题
- **解决**：使用Redis的INCR + EXPIRE实现更精确的限流

---

## 📝 实现建议

### 阶段一：开发阶段（单机部署）

**Redis使用：**
- ✅ 缓存（商品列表、统计数据）
- ✅ 限流（防止接口被刷）
- ❌ 不需要分布式锁（使用数据库事务即可）

**代码实现：**
```java
// 使用数据库事务保证积分操作一致性
@Transactional(rollbackFor = Exception.class)
public void adjustPoints(Long memberId, Integer points, ...) {
    // 使用 SELECT ... FOR UPDATE 加锁
    Member member = memberMapper.selectByIdForUpdate(memberId);
    // ... 执行积分操作
}
```

### 阶段二：生产环境（多实例部署）

**Redis使用：**
- ✅ 缓存（同上）
- ✅ 限流（同上）
- ✅ **分布式锁**（必须添加）
- ✅ **分布式Session**（必须添加）

**代码实现：**
```java
// 使用Redis分布式锁保证积分操作一致性
public void adjustPoints(Long memberId, Integer points, ...) {
    String lockKey = "lock:member:points:" + memberId;
    // ... 获取分布式锁
    try {
        // 执行积分操作
    } finally {
        // 释放锁
    }
}
```

---

## 🎯 总结

### 什么时候必须使用Redis分布式操作？

1. **多实例部署时：**
   - ✅ 分布式锁（积分操作）
   - ✅ 分布式Session
   - ✅ 分布式限流

2. **高并发场景：**
   - ✅ 分布式锁（防止积分操作冲突）
   - ✅ 分布式限流（防止接口被刷）

3. **实时统计需求：**
   - ✅ 分布式计数器（数据大屏）

### 什么时候不需要？

1. **单机部署：**
   - ❌ 分布式锁（使用数据库事务即可）
   - ❌ 分布式Session（使用本地Session即可）

2. **低并发场景：**
   - ❌ 分布式锁（数据库事务足够）

---

## 📚 参考代码

### 完整的分布式锁实现

```java
@Component
@RequiredArgsConstructor
public class DistributedLock {
    private final StringRedisTemplate stringRedisTemplate;
    
    /**
     * 尝试获取锁
     */
    public boolean tryLock(String key, String value, long expireSeconds) {
        return stringRedisTemplate.opsForValue()
            .setIfAbsent(key, value, Duration.ofSeconds(expireSeconds));
    }
    
    /**
     * 释放锁（Lua脚本保证原子性）
     */
    public void releaseLock(String key, String value) {
        String luaScript = 
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "    return redis.call('del', KEYS[1]) " +
            "else " +
            "    return 0 " +
            "end";
        
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(luaScript);
        script.setResultType(Long.class);
        
        stringRedisTemplate.execute(script, 
            Collections.singletonList(key), 
            value);
    }
}
```

---

**建议：** 即使初期单机部署，也建议实现分布式锁的代码，方便后续扩展。只需要在配置中控制是否启用即可。
