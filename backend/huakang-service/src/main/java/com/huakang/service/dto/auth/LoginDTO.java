package com.huakang.service.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录请求DTO
 *
 * @author huakang
 */
@Data
public class LoginDTO {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 用户类型（可选）：admin-管理员，staff-店员
     * 如果不指定，系统会自动判断（先尝试管理员，再尝试店员）
     */
    private String userType;
}
