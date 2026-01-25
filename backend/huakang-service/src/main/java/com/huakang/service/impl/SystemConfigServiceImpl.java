package com.huakang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huakang.common.exception.BusinessException;
import com.huakang.mapper.SystemConfigMapper;
import com.huakang.mapper.entity.SystemConfig;
import com.huakang.service.dto.system.SystemConfigVO;
import com.huakang.service.dto.system.UpdateSystemConfigDTO;
import com.huakang.service.service.SystemConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 系统配置服务实现类
 *
 * @author huakang
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemConfigServiceImpl implements SystemConfigService {

    private final SystemConfigMapper systemConfigMapper;

    // 企业微信二维码配置键
    private static final String WECHAT_QRCODE_KEY = "wechat_qrcode_url";

    @Override
    @Cacheable(value = "system_configs", key = "'all'")
    public List<SystemConfigVO> getAllConfigs() {
        List<SystemConfig> configs = systemConfigMapper.selectList(null);
        return configs.stream()
                .map(this::convertToVO)
                .toList();
    }

    @Override
    @Cacheable(value = "system_configs", key = "#configKey")
    public SystemConfigVO getConfigByKey(String configKey) {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getConfigKey, configKey);
        SystemConfig config = systemConfigMapper.selectOne(wrapper);
        
        if (config == null) {
            throw new BusinessException("配置不存在: " + configKey);
        }
        
        return convertToVO(config);
    }

    @Override
    @Cacheable(value = "system_configs", key = "'group:' + #configGroup")
    public List<SystemConfigVO> getConfigsByGroup(String configGroup) {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getConfigGroup, configGroup);
        List<SystemConfig> configs = systemConfigMapper.selectList(wrapper);
        
        return configs.stream()
                .map(this::convertToVO)
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "system_configs", allEntries = true)
    public SystemConfigVO updateConfig(String configKey, UpdateSystemConfigDTO updateDTO) {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getConfigKey, configKey);
        SystemConfig config = systemConfigMapper.selectOne(wrapper);
        
        if (config == null) {
            throw new BusinessException("配置不存在: " + configKey);
        }

        // 更新配置值
        config.setConfigValue(updateDTO.getConfigValue());
        if (updateDTO.getDescription() != null) {
            config.setDescription(updateDTO.getDescription());
        }
        // 版本号自增
        config.setVersion(config.getVersion() + 1);

        systemConfigMapper.updateById(config);
        return convertToVO(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "system_configs", allEntries = true)
    public SystemConfigVO uploadWechatQrcode(String imageUrl) {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getConfigKey, WECHAT_QRCODE_KEY);
        SystemConfig config = systemConfigMapper.selectOne(wrapper);

        if (config == null) {
            // 如果配置不存在，创建新配置
            config = new SystemConfig();
            config.setConfigKey(WECHAT_QRCODE_KEY);
            config.setConfigValue(imageUrl);
            config.setConfigType("string");
            config.setConfigGroup("wechat");
            config.setDescription("企业微信二维码URL");
            config.setVersion(1);
            systemConfigMapper.insert(config);
        } else {
            // 更新现有配置
            config.setConfigValue(imageUrl);
            config.setVersion(config.getVersion() + 1);
            systemConfigMapper.updateById(config);
        }

        return convertToVO(config);
    }

    /**
     * 转换为VO
     */
    private SystemConfigVO convertToVO(SystemConfig config) {
        SystemConfigVO vo = new SystemConfigVO();
        BeanUtils.copyProperties(config, vo);
        return vo;
    }
}
