package com.huakang.service.dto.staff;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 店员VO
 *
 * @author huakang
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "店员信息")
public class StaffVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "店员ID", example = "1")
    private Long id;

    @Schema(description = "登录账号", example = "staff001")
    private String username;

    @Schema(description = "店员姓名", example = "张三")
    private String name;

    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;

    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @Schema(description = "部门", example = "销售部")
    private String department;

    @Schema(description = "状态（1=启用，0=禁用）", example = "1")
    private Integer status;

    @Schema(description = "最后登录时间", example = "2025-01-01T10:00:00")
    private LocalDateTime lastLoginAt;

    @Schema(description = "最后登录IP", example = "192.168.1.100")
    private String lastLoginIp;

    @Schema(description = "创建时间", example = "2025-01-01T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间", example = "2025-01-01T10:00:00")
    private LocalDateTime updatedAt;
}
