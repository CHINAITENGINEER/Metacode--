package com.huakang.service.dto.member;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @Size(max = 100, message = "昵称长度不能超过100个字符")
    private String nickname;

    /**
     * 微信头像URL
     */
    private String avatar;

    /**
     * 手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 初始积分（默认0）
     */
    @Min(value = 0, message = "初始积分不能为负数")
    @Max(value = 10000, message = "初始积分不能超过10000")
    private Integer initialPoints = 0;
}
