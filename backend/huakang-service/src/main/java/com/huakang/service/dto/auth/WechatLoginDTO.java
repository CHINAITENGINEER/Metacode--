package com.huakang.service.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 微信登录请求DTO
 *
 * @author huakang
 */
@Data
public class WechatLoginDTO {

    /**
     * 微信登录凭证
     */
    @NotBlank(message = "登录凭证不能为空")
    private String code;

    /**
     * 加密用户数据
     */
    private String encryptedData;

    /**
     * 加密算法的初始向量
     */
    private String iv;
}