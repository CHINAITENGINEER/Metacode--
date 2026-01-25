package com.huakang.mapper.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 店员实体类
 *
 * @author huakang
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("staffs")
public class Staff {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 登录账号
     */
    @TableField("username")
    private String username;

    /**
     * 密码哈希值（BCrypt）
     */
    @TableField("password_hash")
    private String passwordHash;

    /**
     * 店员姓名
     */
    @TableField("name")
    private String name;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 手机号
     */
    @TableField("phone")
    private String phone;

    /**
     * 部门（预留）
     */
    @TableField("department")
    private String department;

    /**
     * 状态：1=启用，0=禁用
     */
    @TableField("status")
    private Integer status;

    /**
     * 最后登录时间
     */
    @TableField("last_login_at")
    private LocalDateTime lastLoginAt;

    /**
     * 最后登录IP
     */
    @TableField("last_login_ip")
    private String lastLoginIp;

    /**
     * 连续登录失败次数
     */
    @TableField("login_fail_count")
    private Integer loginFailCount;

    /**
     * 账号锁定到期时间
     */
    @TableField("locked_until")
    private LocalDateTime lockedUntil;

    /**
     * 是否删除：0=否，1=是
     */
    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
