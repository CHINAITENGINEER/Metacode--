package com.huakang.admin.controller;

import com.huakang.common.core.Result;
import com.huakang.service.annotation.RateLimiter;
import com.huakang.service.dto.auth.LoginDTO;
import com.huakang.service.dto.auth.LoginVO;
import com.huakang.service.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 *
 * @author huakang
 */
@Tag(name = "认证管理", description = "统一登录接口（支持管理员和店员）")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final com.huakang.service.utils.JwtUtils jwtUtils;

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
        return ip;
    }

    /**
     * 统一登录接口
     * 支持管理员和店员登录，可通过userType参数指定类型，或自动判断
     */
    @Operation(summary = "统一登录", description = "管理员和店员统一登录接口。可通过userType参数指定类型（admin/staff），如果不指定则自动判断（先尝试管理员，再尝试店员）")
    @PostMapping("/login")
    @RateLimiter(key = "login", limit = 5, windowSeconds = 60) // 每分钟最多5次
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        String ipAddress = getClientIp(request);
        LoginVO loginVO = authService.login(
                loginDTO.getUsername(),
                loginDTO.getPassword(),
                loginDTO.getUserType(),
                ipAddress
        );
        return Result.success("登录成功", loginVO);
    }
}
