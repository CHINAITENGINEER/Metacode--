package com.huakang.service.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "系统配置信息")
public class SystemConfigVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "配置ID", example = "1")
    private Long id;

    @Schema(description = "配置键（唯一标识）", example = "banner_images")
    private String configKey;

    @Schema(description = "配置值", example = "[\"https://example.com/banner1.jpg\"]")
    private String configValue;

    @Schema(description = "配置类型（string=字符串，number=数字，json=JSON对象，boolean=布尔值）", example = "json")
    private String configType;

    @Schema(description = "配置分组（用于分类管理）", example = "miniapp")
    private String configGroup;

    @Schema(description = "配置说明", example = "小程序首页轮播图")
    private String description;

    @Schema(description = "配置版本号（用于缓存更新）", example = "1")
    private Integer version;

    @Schema(description = "更新时间", example = "2025-01-01T10:00:00")
    private LocalDateTime updatedAt;
}
