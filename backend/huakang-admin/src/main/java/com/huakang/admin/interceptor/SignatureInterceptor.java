package com.huakang.admin.interceptor;

import com.huakang.common.exception.BusinessException;
import com.huakang.service.annotation.RequireSignature;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

/**
 * 请求签名校验拦截器
 *
 * 校验规则（简化版，便于前后端接入）：
 * - Header 必须包含：X-Timestamp, X-Signature
 * - 检查时间戳是否在允许的时间偏移内（默认5分钟）
 * - 使用 HMAC-SHA256(secret, timestamp + method + path) 计算签名并与 X-Signature 比较
 *
 * 注意：前端需要按照同样规则生成签名。
 *
 * @author huakang
 */
@Slf4j
@Component
public class SignatureInterceptor implements HandlerInterceptor {

    /**
     * 签名密钥，默认复用JWT密钥，生产环境建议单独配置
     */
    @Value("${jwt.secret}")
    private String signatureSecret;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        // 方法或类上没有 @RequireSignature 注解则直接放行
        RequireSignature methodAnno = handlerMethod.getMethodAnnotation(RequireSignature.class);
        RequireSignature classAnno = handlerMethod.getBeanType().getAnnotation(RequireSignature.class);
        RequireSignature requireSignature = methodAnno != null ? methodAnno : classAnno;
        if (requireSignature == null) {
            return true;
        }

        String timestampStr = request.getHeader("X-Timestamp");
        String signature = request.getHeader("X-Signature");
        if (timestampStr == null || signature == null) {
            throw new BusinessException("缺少请求签名信息");
        }

        long now = Instant.now().getEpochSecond();
        long ts;
        try {
            ts = Long.parseLong(timestampStr);
        } catch (NumberFormatException e) {
            throw new BusinessException("无效的时间戳");
        }

        long skew = Math.abs(now - ts);
        if (skew > requireSignature.maxTimestampSkewSeconds()) {
            throw new BusinessException("请求已过期，请重新发起请求");
        }

        String method = request.getMethod();
        String path = request.getRequestURI();
        String payload = timestampStr + "|" + method + "|" + path;
        String expectedSignature = hmacSha256Hex(signatureSecret, payload);

        if (!expectedSignature.equalsIgnoreCase(signature)) {
            log.warn("请求签名校验失败, path={}, method={}, timestamp={}, expectedSignature={}",
                    path, method, timestampStr, expectedSignature);
            throw new BusinessException("请求签名无效");
        }

        return true;
    }

    private String hmacSha256Hex(String secret, String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] result = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder(result.length * 2);
        for (byte b : result) {
            String hex = Integer.toHexString(b & 0xff);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}

