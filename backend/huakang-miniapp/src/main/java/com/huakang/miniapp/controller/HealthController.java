package com.huakang.miniapp.controller;

import com.huakang.common.core.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 * 用于负载均衡器和监控系统检查服务状态
 */
@Slf4j
@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
@Tag(name = "健康检查", description = "服务健康检查接口")
public class HealthController {

    private final JdbcTemplate jdbcTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    @GetMapping
    @Operation(summary = "健康检查", description = "检查服务及依赖组件的健康状态")
    public Result<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        boolean isHealthy = true;

        // 检查数据库连接
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            health.put("database", Map.of(
                "status", "UP",
                "message", "数据库连接正常"
            ));
        } catch (Exception e) {
            log.error("数据库健康检查失败", e);
            health.put("database", Map.of(
                "status", "DOWN",
                "message", "数据库连接失败: " + e.getMessage()
            ));
            isHealthy = false;
        }

        // 检查Redis连接
        try {
            redisTemplate.opsForValue().set("health:check", "ok");
            String value = (String) redisTemplate.opsForValue().get("health:check");
            if ("ok".equals(value)) {
                health.put("redis", Map.of(
                    "status", "UP",
                    "message", "Redis连接正常"
                ));
            } else {
                health.put("redis", Map.of(
                    "status", "DOWN",
                    "message", "Redis读写异常"
                ));
                isHealthy = false;
            }
        } catch (Exception e) {
            log.error("Redis健康检查失败", e);
            health.put("redis", Map.of(
                "status", "DOWN",
                "message", "Redis连接失败: " + e.getMessage()
            ));
            isHealthy = false;
        }

        // 整体状态
        health.put("status", isHealthy ? "UP" : "DOWN");
        health.put("application", "huakang-miniapp");
        health.put("timestamp", System.currentTimeMillis());

        // 无论健康与否，都返回状态信息
        if (isHealthy) {
            return Result.success(health);
        } else {
            return Result.success("服务部分组件不健康", health);
        }
    }

    @GetMapping("/ping")
    @Operation(summary = "简单心跳检查", description = "快速检查服务是否存活")
    public Result<String> ping() {
        return Result.success("pong");
    }
}
