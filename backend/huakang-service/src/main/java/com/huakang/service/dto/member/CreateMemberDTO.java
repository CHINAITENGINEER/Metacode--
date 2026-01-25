package com.huakang.service.dto.member;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 创建会员DTO
 *
 * @author huakang
 */
@Data
public class CreateMemberDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 微信OpenID（可选，小程序端自动注册时使用）
     */
    private String openid;

    /**
     * 微信昵称
     */
    @NotBlank(message = "昵称不能为空")
    private String nickname;

    /**
     * 微信头像URL
     */
    private String avatar;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 初始积分（默认0）
     */
    private Integer initialPoints = 0;
}
