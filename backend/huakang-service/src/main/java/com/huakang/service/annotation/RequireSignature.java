package com.huakang.service.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 请求签名校验注解
 * <p>
 * 标记在需要进行签名验证的接口上，防止重放攻击和请求被篡改。
 *
 * @author huakang
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireSignature {

    /**
     * 时间戳允许的最大偏移量（秒），用于防止重放攻击
     */
    long maxTimestampSkewSeconds() default 300L; // 默认5分钟
}

