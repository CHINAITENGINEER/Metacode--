package com.huakang.service.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "创建会员请求参数")
public class CreateMemberDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 微信OpenID（可选，小程序端自动注册时使用）
     */
    @Schema(description = "微信OpenID（可选，小程序端自动注册时使用）", example = "oxxxxxxxxxxxxxxxxxxxxxx")
    private String openid;

    /**
     * 微信昵称
     */
    @Schema(description = "会员昵称", example = "张三", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "昵称不能为空")
    @Size(max = 100, message = "昵称长度不能超过100个字符")
    private String nickname;

    /**
     * 微信头像URL
     */
    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;

    /**
     * 手机号
     */
    @Schema(description = "手机号（11位，1开头）", example = "13800138000")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 初始积分（默认0，范围0-10000）
     */
    @Schema(description = "初始积分（默认0，范围0-10000）", example = "0")
    @Min(value = 0, message = "初始积分不能为负数")
    @Max(value = 10000, message = "初始积分不能超过10000")
    private Integer initialPoints = 0;
}
