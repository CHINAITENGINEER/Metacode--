package com.huakang.service.service;

import com.huakang.service.dto.auth.WechatLoginDTO;
import com.huakang.service.dto.auth.WechatLoginVO;

/**
 * 微信登录服务接口
 *
 * @author huakang
 */
public interface WechatAuthService {

    /**
     * 微信登录
     *
     * @param loginDTO 登录信息
     * @return 登录结果
     */
    WechatLoginVO wechatLogin(WechatLoginDTO loginDTO);
}