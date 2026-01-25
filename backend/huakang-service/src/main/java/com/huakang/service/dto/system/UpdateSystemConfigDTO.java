package com.huakang.service.dto.system;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 更新系统配置DTO
 *
 * @author huakang
 */
@Data
public class UpdateSystemConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 配置值
     */
    @NotBlank(message = "配置值不能为空")
    private String configValue;

    /**
     * 配置说明（可选）
     */
    private String description;
}
