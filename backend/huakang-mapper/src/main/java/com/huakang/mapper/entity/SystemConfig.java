package com.huakang.mapper.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 系统配置实体类
 *
 * @author huakang
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("system_configs")
public class SystemConfig {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 配置键
     */
    @TableField("config_key")
    private String configKey;

    /**
     * 配置值
     */
    @TableField("config_value")
    private String configValue;

    /**
     * 配置类型：string/number/json/boolean
     */
    @TableField("config_type")
    private String configType;

    /**
     * 配置分组
     */
    @TableField("config_group")
    private String configGroup;

    /**
     * 配置说明
     */
    @TableField("description")
    private String description;

    /**
     * 配置版本号
     */
    @TableField("version")
    private Integer version;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
