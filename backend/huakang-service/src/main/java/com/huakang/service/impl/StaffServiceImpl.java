package com.huakang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huakang.common.core.PageResult;
import com.huakang.common.exception.BusinessException;
import com.huakang.mapper.StaffMapper;
import com.huakang.mapper.entity.Staff;
import com.huakang.service.dto.auth.LoginVO;
import com.huakang.service.dto.staff.CreateStaffDTO;
import com.huakang.service.dto.staff.ResetPasswordDTO;
import com.huakang.service.dto.staff.StaffVO;
import com.huakang.service.service.StaffService;
import com.huakang.service.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 店员服务实现类
 *
 * @author huakang
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    private final StaffMapper staffMapper;
    private final JwtUtils jwtUtils;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 登录失败锁定配置
    private static final int MAX_LOGIN_FAIL_COUNT = 5; // 最大失败次数
    private static final int LOCK_MINUTES = 30; // 锁定30分钟

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginVO login(String username, String password, String ipAddress) {
        // 1. 查询店员
        Staff staff = getByUsername(username);
        if (staff == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 2. 检查账号状态
        if (staff.getStatus() == null || staff.getStatus() == 0) {
            throw new BusinessException("账号已被禁用，请联系管理员");
        }

        // 3. 检查账号是否被锁定
        if (isAccountLocked(staff)) {
            LocalDateTime lockedUntil = staff.getLockedUntil();
            throw new BusinessException("账号已被锁定，请于 " + lockedUntil + " 后重试");
        }

        // 4. 验证密码
        if (!passwordEncoder.matches(password, staff.getPasswordHash())) {
            // 密码错误，增加失败次数
            incrementLoginFailCount(staff.getId());
            Staff updatedStaff = getByUsername(username);
            
            // 检查是否需要锁定
            if (updatedStaff.getLoginFailCount() >= MAX_LOGIN_FAIL_COUNT) {
                lockAccount(updatedStaff.getId(), LOCK_MINUTES);
                throw new BusinessException("连续登录失败次数过多，账号已锁定30分钟");
            }
            
            int remainingAttempts = MAX_LOGIN_FAIL_COUNT - updatedStaff.getLoginFailCount();
            throw new BusinessException("用户名或密码错误，剩余尝试次数：" + remainingAttempts);
        }

        // 5. 密码正确，重置失败次数
        resetLoginFailCount(staff.getId());

        // 6. 更新最后登录信息
        updateLastLoginInfo(staff.getId(), ipAddress);

        // 7. 生成JWT Token
        String token = jwtUtils.generateToken(staff.getId(), staff.getUsername(), "staff");

        // 8. 返回登录信息
        return LoginVO.builder()
                .token(token)
                .userId(staff.getId())
                .username(staff.getUsername())
                .name(staff.getName())
                .role("staff")
                .expiresIn(7200L) // 2小时
                .build();
    }

    /**
     * 根据用户名查询店员（带缓存）
     * 缓存key: staffs::username:{username}
     * 过期时间: 10分钟（在RedisConfig中配置）
     */
    @Override
    @Cacheable(value = "staffs", key = "'username:' + #username", unless = "#result == null")
    public Staff getByUsername(String username) {
        LambdaQueryWrapper<Staff> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Staff::getUsername, username);
        return staffMapper.selectOne(wrapper);
    }

    /**
     * 更新最后登录信息（清除缓存）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "staffs", allEntries = true)
    public void updateLastLoginInfo(Long staffId, String ipAddress) {
        Staff staff = new Staff();
        staff.setId(staffId);
        staff.setLastLoginAt(LocalDateTime.now());
        staff.setLastLoginIp(ipAddress);
        staffMapper.updateById(staff);
    }

    /**
     * 增加登录失败次数（清除缓存）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "staffs", allEntries = true)
    public void incrementLoginFailCount(Long staffId) {
        Staff staff = staffMapper.selectById(staffId);
        if (staff != null) {
            Staff updateStaff = new Staff();
            updateStaff.setId(staffId);
            updateStaff.setLoginFailCount((staff.getLoginFailCount() == null ? 0 : staff.getLoginFailCount()) + 1);
            staffMapper.updateById(updateStaff);
        }
    }

    /**
     * 重置登录失败次数（清除缓存）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "staffs", allEntries = true)
    public void resetLoginFailCount(Long staffId) {
        Staff staff = new Staff();
        staff.setId(staffId);
        staff.setLoginFailCount(0);
        staff.setLockedUntil(null);
        staffMapper.updateById(staff);
    }

    /**
     * 锁定账号（清除缓存）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "staffs", allEntries = true)
    public void lockAccount(Long staffId, int lockMinutes) {
        Staff staff = new Staff();
        staff.setId(staffId);
        staff.setLockedUntil(LocalDateTime.now().plusMinutes(lockMinutes));
        staffMapper.updateById(staff);
    }

    @Override
    public boolean isAccountLocked(Staff staff) {
        if (staff.getLockedUntil() == null) {
            return false;
        }
        return LocalDateTime.now().isBefore(staff.getLockedUntil());
    }

    @Override
    public PageResult<StaffVO> listStaffs(Integer page, Integer size) {
        Page<Staff> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<Staff> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Staff::getIsDeleted, 0)
                .orderByDesc(Staff::getCreatedAt);
        Page<Staff> result = staffMapper.selectPage(pageObj, wrapper);

        PageResult<StaffVO> pageResult = new PageResult<>();
        pageResult.setTotal(result.getTotal());
        pageResult.setPages(result.getPages());
        pageResult.setCurrent(result.getCurrent());
        pageResult.setSize(result.getSize());
        pageResult.setRecords(result.getRecords().stream()
                .map(this::convertToVO)
                .toList());

        return pageResult;
    }

    /**
     * 获取店员详情（带缓存）
     * 缓存key: staffs::id:{staffId}
     * 过期时间: 10分钟（在RedisConfig中配置）
     */
    @Override
    @Cacheable(value = "staffs", key = "'id:' + #staffId", unless = "#result == null")
    public StaffVO getStaffById(Long staffId) {
        Staff staff = staffMapper.selectById(staffId);
        if (staff == null || staff.getIsDeleted() == 1) {
            throw new BusinessException("店员不存在");
        }
        return convertToVO(staff);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StaffVO createStaff(CreateStaffDTO createDTO) {
        // 检查用户名是否已存在
        Staff existingStaff = getByUsername(createDTO.getUsername());
        if (existingStaff != null) {
            throw new BusinessException("该用户名已存在");
        }

        // 创建店员
        Staff staff = new Staff();
        staff.setUsername(createDTO.getUsername());
        staff.setPasswordHash(passwordEncoder.encode(createDTO.getPassword()));
        staff.setName(createDTO.getName());
        staff.setEmail(createDTO.getEmail());
        staff.setPhone(createDTO.getPhone());
        staff.setDepartment(createDTO.getDepartment());
        staff.setStatus(1); // 默认启用
        staffMapper.insert(staff);

        return convertToVO(staff);
    }

    /**
     * 重置密码（清除缓存）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "staffs", key = "'id:' + #staffId")
    public void resetPassword(Long staffId, ResetPasswordDTO resetDTO) {
        Staff staff = staffMapper.selectById(staffId);
        if (staff == null || staff.getIsDeleted() == 1) {
            throw new BusinessException("店员不存在");
        }

        Staff updateStaff = new Staff();
        updateStaff.setId(staffId);
        updateStaff.setPasswordHash(passwordEncoder.encode(resetDTO.getNewPassword()));
        updateStaff.setLoginFailCount(0); // 重置失败次数
        updateStaff.setLockedUntil(null); // 解除锁定
        staffMapper.updateById(updateStaff);
    }

    /**
     * 切换店员状态（清除缓存）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "staffs", key = "'id:' + #staffId")
    public void toggleStatus(Long staffId, Integer status) {
        Staff staff = staffMapper.selectById(staffId);
        if (staff == null || staff.getIsDeleted() == 1) {
            throw new BusinessException("店员不存在");
        }

        if (status != 0 && status != 1) {
            throw new BusinessException("无效的状态值");
        }

        Staff updateStaff = new Staff();
        updateStaff.setId(staffId);
        updateStaff.setStatus(status);
        staffMapper.updateById(updateStaff);
    }

    /**
     * 转换为VO
     */
    private StaffVO convertToVO(Staff staff) {
        StaffVO vo = new StaffVO();
        BeanUtils.copyProperties(staff, vo);
        return vo;
    }
}
