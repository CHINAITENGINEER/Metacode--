package com.huakang.service.dto.staff;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 创建店员DTO
 *
 * @author huakang
 */
@Data
@Schema(description = "创建店员请求参数")
public class CreateStaffDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 登录账号
     * 用于店员登录系统的唯一标识
     */
    @Schema(description = "登录账号（唯一）", example = "staff001", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "登录账号不能为空")
    private String username;

    /**
     * 初始密码
     * 店员首次登录使用，建议首次登录后修改
     */
    @Schema(description = "初始密码", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "初始密码不能为空")
    private String password;

    /**
     * 店员姓名
     * 真实姓名，用于显示和记录操作人
     */
    @Schema(description = "店员姓名", example = "张三", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "姓名不能为空")
    private String name;

    /**
     * 邮箱
     * 可选，用于接收通知
     */
    @Schema(description = "邮箱（可选）", example = "zhangsan@example.com")
    private String email;

    /**
     * 手机号
     * 可选，用于联系
     */
    @Schema(description = "手机号（可选）", example = "13800138000")
    private String phone;

    /**
     * 部门（预留）
     * 预留字段，用于未来的部门管理功能
     */
    @Schema(description = "部门（预留字段）", example = "销售部")
    private String department;
}
