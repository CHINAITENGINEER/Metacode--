package com.huakang.mapper.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 会员实体类
 *
 * @author huakang
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("members")
public class Member {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 微信OpenID
     */
    @TableField("openid")
    private String openid;

    /**
     * 微信昵称
     */
    @TableField("nickname")
    private String nickname;

    /**
     * 微信头像URL
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 手机号
     */
    @TableField("phone")
    private String phone;

    /**
     * 当前积分总额
     */
    @TableField("total_points")
    private Integer totalPoints;

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
