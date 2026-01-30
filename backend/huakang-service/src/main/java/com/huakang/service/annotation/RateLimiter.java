package com.huakang.service.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口限流注解
 * <p>
 * 用于限制单位时间内的请求次数，防止暴力破解和恶意刷接口。
 *
 * @author huakang
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimiter {

    /**
     * 限流键前缀，用于区分不同的限流场景
     */
    String key();

    /**
     * 在时间窗口内允许的最大请求次数
     */
    int limit();

    /**
     * 时间窗口，单位：秒
     */
    int windowSeconds();
}

