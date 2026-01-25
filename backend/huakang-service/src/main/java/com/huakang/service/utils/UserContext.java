package com.huakang.service.utils;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 用户上下文工具类
 * 用于从请求中获取当前登录用户信息
 *
 * @author huakang
 */
@Slf4j
@Component
public class UserContext {

    private final JwtUtils jwtUtils;

    public UserContext(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    /**
     * 从请求头中获取Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 获取当前用户ID
     */
    public Long getCurrentUserId(HttpServletRequest request) {
        try {
            String token = getTokenFromRequest(request);
            if (token == null) {
                return null;
            }
            return jwtUtils.getUserIdFromToken(token);
        } catch (Exception e) {
            log.warn("获取用户ID失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取当前用户名
     */
    public String getCurrentUsername(HttpServletRequest request) {
        try {
            String token = getTokenFromRequest(request);
            if (token == null) {
                return null;
            }
            return jwtUtils.getUsernameFromToken(token);
        } catch (Exception e) {
            log.warn("获取用户名失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取当前用户角色
     */
    public String getCurrentUserRole(HttpServletRequest request) {
        try {
            String token = getTokenFromRequest(request);
            if (token == null) {
                return null;
            }
            return jwtUtils.getRoleFromToken(token);
        } catch (Exception e) {
            log.warn("获取用户角色失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取当前用户信息（Claims）
     */
    public Claims getCurrentUserClaims(HttpServletRequest request) {
        try {
            String token = getTokenFromRequest(request);
            if (token == null) {
                return null;
            }
            return jwtUtils.getClaimsFromToken(token);
        } catch (Exception e) {
            log.warn("获取用户信息失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 判断是否为管理员
     */
    public boolean isAdmin(HttpServletRequest request) {
        String role = getCurrentUserRole(request);
        return "admin".equals(role);
    }

    /**
     * 判断是否为店员
     */
    public boolean isStaff(HttpServletRequest request) {
        String role = getCurrentUserRole(request);
        return "staff".equals(role);
    }
}
