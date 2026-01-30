package com.huakang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huakang.common.exception.BusinessException;
import com.huakang.mapper.AdminMapper;
import com.huakang.mapper.entity.Admin;
import com.huakang.service.dto.auth.LoginVO;
import com.huakang.service.service.AdminService;
import com.huakang.service.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 管理员服务实现类
 *
 * @author huakang
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminMapper adminMapper;
    private final JwtUtils jwtUtils;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 登录失败锁定配置
    private static final int MAX_LOGIN_FAIL_COUNT = 5; // 最大失败次数
    private static final int LOCK_MINUTES = 30; // 锁定30分钟

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginVO login(String username, String password, String ipAddress) {
        // 1. 查询管理员
        Admin admin = getByUsername(username);
        if (admin == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 2. 检查账号是否被锁定
        if (isAccountLocked(admin)) {
            LocalDateTime lockedUntil = admin.getLockedUntil();
            throw new BusinessException("账号已被锁定，请于 " + lockedUntil + " 后重试");
        }

        // 3. 验证密码
        if (!passwordEncoder.matches(password, admin.getPasswordHash())) {
            // 密码错误，增加失败次数
            incrementLoginFailCount(admin.getId());
            Admin updatedAdmin = getByUsername(username);
            
            // 检查是否需要锁定
            if (updatedAdmin.getLoginFailCount() >= MAX_LOGIN_FAIL_COUNT) {
                lockAccount(updatedAdmin.getId(), LOCK_MINUTES);
                throw new BusinessException("连续登录失败次数过多，账号已锁定30分钟");
            }
            
            int remainingAttempts = MAX_LOGIN_FAIL_COUNT - updatedAdmin.getLoginFailCount();
            throw new BusinessException("用户名或密码错误，剩余尝试次数：" + remainingAttempts);
        }

        // 4. 密码正确，重置失败次数
        resetLoginFailCount(admin.getId());

        // 5. 更新最后登录信息
        updateLastLoginInfo(admin.getId(), ipAddress);

        // 6. 生成JWT Token
        String token = jwtUtils.generateToken(admin.getId(), admin.getUsername(), "admin");

        // 7. 返回登录信息
        return LoginVO.builder()
                .token(token)
                .userId(admin.getId())
                .username(admin.getUsername())
                .name(admin.getName())
                .role("admin")
                .expiresIn(7200L) // 2小时
                .build();
    }

    /**
     * 根据用户名查询管理员（带缓存）
     * 缓存key: admins::username:{username}
     * 过期时间: 10分钟（在RedisConfig中配置）
     */
    @Override
    @Cacheable(value = "admins", key = "'username:' + #username", unless = "#result == null")
    public Admin getByUsername(String username) {
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getUsername, username);
        return adminMapper.selectOne(wrapper);
    }

    /**
     * 更新最后登录信息（清除缓存）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "admins", allEntries = true)
    public void updateLastLoginInfo(Long adminId, String ipAddress) {
        Admin admin = new Admin();
        admin.setId(adminId);
        admin.setLastLoginAt(LocalDateTime.now());
        admin.setLastLoginIp(ipAddress);
        adminMapper.updateById(admin);
    }

    /**
     * 增加登录失败次数（清除缓存）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "admins", allEntries = true)
    public void incrementLoginFailCount(Long adminId) {
        Admin admin = adminMapper.selectById(adminId);
        if (admin != null) {
            Admin updateAdmin = new Admin();
            updateAdmin.setId(adminId);
            updateAdmin.setLoginFailCount((admin.getLoginFailCount() == null ? 0 : admin.getLoginFailCount()) + 1);
            adminMapper.updateById(updateAdmin);
        }
    }

    /**
     * 重置登录失败次数（清除缓存）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "admins", allEntries = true)
    public void resetLoginFailCount(Long adminId) {
        Admin admin = new Admin();
        admin.setId(adminId);
        admin.setLoginFailCount(0);
        admin.setLockedUntil(null);
        adminMapper.updateById(admin);
    }

    /**
     * 锁定账号（清除缓存）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "admins", allEntries = true)
    public void lockAccount(Long adminId, int lockMinutes) {
        Admin admin = new Admin();
        admin.setId(adminId);
        admin.setLockedUntil(LocalDateTime.now().plusMinutes(lockMinutes));
        adminMapper.updateById(admin);
    }

    @Override
    public boolean isAccountLocked(Admin admin) {
        if (admin.getLockedUntil() == null) {
            return false;
        }
        return LocalDateTime.now().isBefore(admin.getLockedUntil());
    }
}
