package com.huakang.service.impl;

import com.huakang.common.exception.BusinessException;
import com.huakang.service.dto.auth.LoginVO;
import com.huakang.service.service.AdminService;
import com.huakang.service.service.AuthService;
import com.huakang.service.service.StaffService;
import com.huakang.service.utils.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 统一认证服务实现类
 *
 * @author huakang
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AdminService adminService;
    private final StaffService staffService;
    private final JwtUtils jwtUtils;

    @Override
    public LoginVO login(String username, String password, String userType, String ipAddress) {
        // 如果指定了用户类型，直接使用对应的服务
        if ("admin".equalsIgnoreCase(userType)) {
            return adminService.login(username, password, ipAddress);
        } else if ("staff".equalsIgnoreCase(userType)) {
            return staffService.login(username, password, ipAddress);
        }

        // 如果没有指定用户类型，自动判断：先尝试管理员，再尝试店员
        try {
            return adminService.login(username, password, ipAddress);
        } catch (BusinessException e) {
            // 管理员登录失败，尝试店员登录
            log.debug("管理员登录失败，尝试店员登录：{}", e.getMessage());
            return staffService.login(username, password, ipAddress);
        }
    }

    @Override
    public LoginVO refreshToken(String token) {
        try {
            // 尝试解析Token（即使已过期）
            Long userId;
            String username;
            String role;

            try {
                // 先尝试正常解析（Token未过期）
                userId = jwtUtils.getUserIdFromToken(token);
                username = jwtUtils.getUsernameFromToken(token);
                role = jwtUtils.getRoleFromToken(token);
            } catch (ExpiredJwtException e) {
                // Token已过期，但从Claims中获取信息（允许刷新已过期的Token）
                log.debug("Token已过期，尝试刷新: {}", e.getMessage());
                userId = e.getClaims().get("userId", Long.class);
                username = e.getClaims().getSubject();
                role = e.getClaims().get("role", String.class);
            }

            if (userId == null || username == null || role == null) {
                throw new BusinessException("Token无效，无法刷新");
            }

            // 生成新Token
            String newToken = jwtUtils.generateToken(userId, username, role);

            // 构建返回对象
            return LoginVO.builder()
                    .token(newToken)
                    .userId(userId)
                    .username(username)
                    .role(role)
                    .expiresIn(7200L) // 2小时
                    .build();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.warn("刷新Token失败: {}", e.getMessage());
            throw new BusinessException("Token无效，请重新登录");
        }
    }
}
