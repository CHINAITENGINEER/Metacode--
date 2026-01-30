package com.huakang.admin.config;

import com.huakang.admin.interceptor.SignatureInterceptor;
import com.huakang.service.interceptor.RoleCheckInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 *
 * @author huakang
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final RoleCheckInterceptor roleCheckInterceptor;
    private final SignatureInterceptor signatureInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 角色权限拦截
        registry.addInterceptor(roleCheckInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/auth/**",           // 登录接口
                        "/doc.html",          // API文档
                        "/swagger-ui/**",     // Swagger UI
                        "/swagger-ui.html",
                        "/v3/api-docs/**",    // OpenAPI规范
                        "/swagger-resources/**",
                        "/webjars/**"
                );

        // 请求签名校验拦截（仅对标记 @RequireSignature 的接口生效）
        registry.addInterceptor(signatureInterceptor)
                .addPathPatterns("/**");
    }
}
