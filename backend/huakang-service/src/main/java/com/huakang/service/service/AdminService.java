package com.huakang.service.service;

import com.huakang.mapper.entity.Admin;
import com.huakang.service.dto.auth.LoginVO;

/**
 * 管理员服务接口
 *
 * @author huakang
 */
public interface AdminService {

    /**
     * 管理员登录
     *
     * @param username 用户名
     * @param password 密码
     * @param ipAddress 登录IP
     * @return 登录信息
     */
    LoginVO login(String username, String password, String ipAddress);

    /**
     * 根据用户名查询管理员
     *
     * @param username 用户名
     * @return 管理员信息
     */
    Admin getByUsername(String username);

    /**
     * 更新最后登录信息
     *
     * @param adminId 管理员ID
     * @param ipAddress IP地址
     */
    void updateLastLoginInfo(Long adminId, String ipAddress);

    /**
     * 增加登录失败次数
     *
     * @param adminId 管理员ID
     */
    void incrementLoginFailCount(Long adminId);

    /**
     * 重置登录失败次数
     *
     * @param adminId 管理员ID
     */
    void resetLoginFailCount(Long adminId);

    /**
     * 锁定账号
     *
     * @param adminId 管理员ID
     * @param lockMinutes 锁定分钟数
     */
    void lockAccount(Long adminId, int lockMinutes);

    /**
     * 检查账号是否被锁定
     *
     * @param admin 管理员信息
     * @return 是否被锁定
     */
    boolean isAccountLocked(Admin admin);
}
