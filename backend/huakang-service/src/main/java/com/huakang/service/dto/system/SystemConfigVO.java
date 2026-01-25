package com.huakang.service.dto.system;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统配置VO
 *
 * @author huakang
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfigVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 配置ID
     */
    private Long id;

    /**
     * 配置键
     */
    private String configKey;

    /**
     * 配置值
     */
    private String configValue;

    /**
     * 配置类型：string/number/json/boolean
     */
    private String configType;

    /**
     * 配置分组
     */
    private String configGroup;

    /**
     * 配置说明
     */
    private String description;

    /**
     * 配置版本号
     */
    private Integer version;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
