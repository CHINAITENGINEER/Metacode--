package com.huakang.service.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录请求DTO
 *
 * @author huakang
 */
@Data
@Schema(description = "登录请求参数")
public class LoginDTO {

    /**
     * 用户名
     * 管理员或店员的登录账号
     */
    @Schema(description = "用户名（登录账号）", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     * 登录密码
     */
    @Schema(description = "密码", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 用户类型（可选）
     * - admin: 管理员
     * - staff: 店员
     * 如果不指定，系统会自动判断（先尝试管理员，再尝试店员）
     */
    @Schema(description = "用户类型（可选，admin=管理员，staff=店员）", 
            example = "admin",
            allowableValues = {"admin", "staff"})
    private String userType;
}
