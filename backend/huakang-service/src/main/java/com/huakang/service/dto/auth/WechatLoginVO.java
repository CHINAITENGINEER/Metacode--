package com.huakang.service.dto.auth;

import com.huakang.service.dto.member.MemberVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微信登录响应VO
 *
 * @author huakang
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WechatLoginVO {

    /**
     * JWT Token
     */
    private String token;

    /**
     * 会员信息
     */
    private MemberVO memberInfo;

    /**
     * 是否为新用户
     */
    private Boolean isNewUser;
}