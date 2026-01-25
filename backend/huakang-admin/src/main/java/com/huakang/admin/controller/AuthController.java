package com.huakang.admin.controller;

import com.huakang.common.core.Result;
import com.huakang.service.dto.auth.LoginDTO;
import com.huakang.service.dto.auth.LoginVO;
import com.huakang.service.service.AdminService;
import com.huakang.service.service.StaffService;
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
@Tag(name = "认证管理", description = "管理员和店员登录接口")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AdminService adminService;
    private final StaffService staffService;

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
     * 管理员登录
     */
    @Operation(summary = "管理员登录", description = "管理员账号密码登录")
    @PostMapping("/login")
    public Result<LoginVO> adminLogin(@Valid @RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        String ipAddress = getClientIp(request);
        LoginVO loginVO = adminService.login(loginDTO.getUsername(), loginDTO.getPassword(), ipAddress);
        return Result.success("登录成功", loginVO);
    }

    /**
     * 店员登录
     */
    @Operation(summary = "店员登录", description = "店员账号密码登录")
    @PostMapping("/staff-login")
    public Result<LoginVO> staffLogin(@Valid @RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        String ipAddress = getClientIp(request);
        LoginVO loginVO = staffService.login(loginDTO.getUsername(), loginDTO.getPassword(), ipAddress);
        return Result.success("登录成功", loginVO);
    }
}
