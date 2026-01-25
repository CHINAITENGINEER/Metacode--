package com.huakang.service.dto.staff;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 创建店员DTO
 *
 * @author huakang
 */
@Data
public class CreateStaffDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 登录账号
     */
    @NotBlank(message = "登录账号不能为空")
    private String username;

    /**
     * 初始密码
     */
    @NotBlank(message = "初始密码不能为空")
    private String password;

    /**
     * 店员姓名
     */
    @NotBlank(message = "姓名不能为空")
    private String name;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 部门（预留）
     */
    private String department;
}
