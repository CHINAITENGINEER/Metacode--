package com.huakang.service.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 会员VO
 *
 * @author huakang
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "会员信息")
public class MemberVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "会员ID", example = "1")
    private Long id;

    @Schema(description = "微信OpenID", example = "oxxxxxxxxxxxxxxxxxxxxxx")
    private String openid;

    @Schema(description = "会员昵称", example = "张三")
    private String nickname;

    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;

    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @Schema(description = "当前积分总额", example = "1000")
    private Integer totalPoints;

    @Schema(description = "创建时间（注册时间）", example = "2025-01-01T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间", example = "2025-01-01T10:00:00")
    private LocalDateTime updatedAt;

    @Schema(description = "关联商品名称（调整积分时如果关联了商品，会返回商品名称）", example = "海尔冰箱")
    private String productName;
}
