package com.huakang.service.interceptor;

import com.huakang.common.exception.BusinessException;
import com.huakang.service.annotation.RequireRole;
import com.huakang.service.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

/**
 * 角色权限检查拦截器
 *
 * @author huakang
 */
@Component
@RequiredArgsConstructor
public class RoleCheckInterceptor implements HandlerInterceptor {

    private final UserContext userContext;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 只处理方法级别的拦截
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        
        // 检查方法上的注解
        RequireRole methodAnnotation = handlerMethod.getMethodAnnotation(RequireRole.class);
        if (methodAnnotation != null) {
            checkRole(request, methodAnnotation.value());
            return true;
        }

        // 检查类上的注解
        RequireRole classAnnotation = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
        if (classAnnotation != null) {
            checkRole(request, classAnnotation.value());
            return true;
        }

        return true;
    }

    /**
     * 检查用户角色
     */
    private void checkRole(HttpServletRequest request, String[] allowedRoles) {
        if (allowedRoles == null || allowedRoles.length == 0) {
            return; // 没有指定角色要求，允许访问
        }

        String currentRole = userContext.getCurrentUserRole(request);
        if (currentRole == null) {
            throw new BusinessException("未登录或Token无效");
        }

        List<String> allowedRolesList = Arrays.asList(allowedRoles);
        if (!allowedRolesList.contains(currentRole)) {
            throw new BusinessException("无权限访问此接口，需要角色：" + Arrays.toString(allowedRoles));
        }
    }
}
