package com.huakang.admin.aspect;

import com.huakang.common.exception.BusinessException;
import com.huakang.service.annotation.RateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;

/**
 * 接口限流切面
 *
 * 使用Redis计数实现简单的固定窗口限流：
 * - key: rate_limiter:{key}:{ip}
 * - value: 在时间窗口内的请求次数
 *
 * @author huakang
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimiterAspect {

    private final RedisTemplate<String, Object> redisTemplate;

    @Around("@annotation(com.huakang.service.annotation.RateLimiter)")
    public Object doRateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            // 非HTTP请求，直接放行
            return joinPoint.proceed();
        }

        HttpServletRequest request = attributes.getRequest();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        RateLimiter rateLimiter = methodSignature.getMethod().getAnnotation(RateLimiter.class);

        String key = buildKey(rateLimiter, request);
        int limit = rateLimiter.limit();
        int windowSeconds = rateLimiter.windowSeconds();

        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1L) {
            // 首次访问时设置过期时间
            redisTemplate.expire(key, Duration.ofSeconds(windowSeconds));
        }

        if (count != null && count > limit) {
            log.warn("接口限流触发：key={}, limit={}, window={}s, currentCount={}",
                    key, limit, windowSeconds, count);
            throw new BusinessException("请求过于频繁，请稍后重试");
        }

        return joinPoint.proceed();
    }

    /**
     * 构建限流Key：rate_limiter:{key}:{ip}
     */
    private String buildKey(RateLimiter rateLimiter, HttpServletRequest request) {
        String clientIp = getClientIp(request);
        return "rate_limiter:" + rateLimiter.key() + ":" + clientIp;
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 处理多级代理的情况，只取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}

