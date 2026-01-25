package com.huakang.service.dto.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 积分调整DTO
 *
 * @author huakang
 */
@Data
public class PointsAdjustDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 变动类型：add=增加, subtract=扣除, set=直接设置
     */
    @NotBlank(message = "变动类型不能为空")
    private String type; // add, subtract, set

    /**
     * 积分值（增加/扣除时为变动值，设置为目标值）
     */
    @NotNull(message = "积分值不能为空")
    private Integer points;

    /**
     * 备注/原因
     */
    @NotBlank(message = "备注不能为空")
    private String remark;
}
