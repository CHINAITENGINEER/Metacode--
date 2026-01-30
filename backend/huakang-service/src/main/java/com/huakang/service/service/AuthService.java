package com.huakang.service.service;

import com.huakang.service.dto.auth.LoginVO;

/**
 * 统一认证服务接口
 *
 * @author huakang
 */
public interface AuthService {

    /**
     * 统一登录接口
     * 自动判断是管理员还是店员登录
     *
     * @param username 用户名
     * @param password 密码
     * @param userType 用户类型（可选）：admin-管理员，staff-店员。如果不指定则自动判断
     * @param ipAddress 登录IP
     * @return 登录信息
     */
    LoginVO login(String username, String password, String userType, String ipAddress);

    /**
     * 刷新Token
     * 使用当前Token刷新获取新Token，延长有效期
     *
     * @param token 当前Token
     * @return 新的登录信息
     */
    LoginVO refreshToken(String token);
}
