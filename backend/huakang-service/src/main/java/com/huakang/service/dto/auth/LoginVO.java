package com.huakang.service.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应VO
 *
 * @author huakang
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "登录响应信息")
public class LoginVO {

    @Schema(description = "JWT Token（用于后续请求的身份认证）", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "用户名（登录账号）", example = "admin")
    private String username;

    @Schema(description = "姓名（真实姓名）", example = "张三")
    private String name;

    @Schema(description = "角色（admin=管理员，staff=店员）", example = "admin")
    private String role;

    @Schema(description = "Token过期时间（秒）", example = "7200")
    private Long expiresIn;
}
