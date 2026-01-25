package com.huakang.service.dto.member;

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
public class MemberVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 会员ID
     */
    private Long id;

    /**
     * 微信OpenID
     */
    private String openid;

    /**
     * 微信昵称
     */
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
     * 当前积分总额
     */
    private Integer totalPoints;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
