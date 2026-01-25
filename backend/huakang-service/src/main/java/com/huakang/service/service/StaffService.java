package com.huakang.service.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huakang.common.core.PageResult;
import com.huakang.mapper.entity.Staff;
import com.huakang.service.dto.auth.LoginVO;
import com.huakang.service.dto.staff.CreateStaffDTO;
import com.huakang.service.dto.staff.ResetPasswordDTO;
import com.huakang.service.dto.staff.StaffVO;

import java.util.List;

/**
 * 店员服务接口
 *
 * @author huakang
 */
public interface StaffService {

    /**
     * 店员登录
     *
     * @param username 用户名
     * @param password 密码
     * @param ipAddress 登录IP
     * @return 登录信息
     */
    LoginVO login(String username, String password, String ipAddress);

    /**
     * 根据用户名查询店员
     *
     * @param username 用户名
     * @return 店员信息
     */
    Staff getByUsername(String username);

    /**
     * 更新最后登录信息
     *
     * @param staffId 店员ID
     * @param ipAddress IP地址
     */
    void updateLastLoginInfo(Long staffId, String ipAddress);

    /**
     * 增加登录失败次数
     *
     * @param staffId 店员ID
     */
    void incrementLoginFailCount(Long staffId);

    /**
     * 重置登录失败次数
     *
     * @param staffId 店员ID
     */
    void resetLoginFailCount(Long staffId);

    /**
     * 锁定账号
     *
     * @param staffId 店员ID
     * @param lockMinutes 锁定分钟数
     */
    void lockAccount(Long staffId, int lockMinutes);

    /**
     * 检查账号是否被锁定
     *
     * @param staff 店员信息
     * @return 是否被锁定
     */
    boolean isAccountLocked(Staff staff);

    /**
     * 分页查询店员列表
     *
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    PageResult<StaffVO> listStaffs(Integer page, Integer size);

    /**
     * 根据ID获取店员详情
     *
     * @param staffId 店员ID
     * @return 店员信息
     */
    StaffVO getStaffById(Long staffId);

    /**
     * 创建店员账号
     *
     * @param createDTO 创建信息
     * @return 店员信息
     */
    StaffVO createStaff(CreateStaffDTO createDTO);

    /**
     * 重置密码
     *
     * @param staffId 店员ID
     * @param resetDTO 重置信息
     */
    void resetPassword(Long staffId, ResetPasswordDTO resetDTO);

    /**
     * 启用/禁用店员账号
     *
     * @param staffId 店员ID
     * @param status 状态：1=启用，0=禁用
     */
    void toggleStatus(Long staffId, Integer status);
}
