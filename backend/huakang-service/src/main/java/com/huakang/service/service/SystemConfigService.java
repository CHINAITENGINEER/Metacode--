package com.huakang.service.service;

import com.huakang.service.dto.system.SystemConfigVO;
import com.huakang.service.dto.system.UpdateSystemConfigDTO;

import java.util.List;

/**
 * 系统配置服务接口
 *
 * @author huakang
 */
public interface SystemConfigService {

    /**
     * 获取所有系统配置
     *
     * @return 配置列表
     */
    List<SystemConfigVO> getAllConfigs();

    /**
     * 根据配置键获取配置值
     *
     * @param configKey 配置键
     * @return 配置信息
     */
    SystemConfigVO getConfigByKey(String configKey);

    /**
     * 根据配置分组获取配置列表
     *
     * @param configGroup 配置分组
     * @return 配置列表
     */
    List<SystemConfigVO> getConfigsByGroup(String configGroup);

    /**
     * 更新系统配置
     *
     * @param configKey 配置键
     * @param updateDTO 更新信息
     * @return 配置信息
     */
    SystemConfigVO updateConfig(String configKey, UpdateSystemConfigDTO updateDTO);

    /**
     * 上传企业微信二维码
     *
     * @param imageUrl 图片URL
     * @return 配置信息
     */
    SystemConfigVO uploadWechatQrcode(String imageUrl);
}
