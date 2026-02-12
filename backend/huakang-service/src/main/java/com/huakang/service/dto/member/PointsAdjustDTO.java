package com.huakang.service.dto.member;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "积分调整请求参数")
public class PointsAdjustDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 变动类型
     * - add: 增加积分（在原有积分基础上增加指定数量）
     * - subtract: 扣除积分（在原有积分基础上扣除指定数量）
     * - set: 直接设置积分（将积分设置为指定数量，不考虑原有积分）
     */
    @Schema(description = "变动类型", 
            example = "add",
            allowableValues = {"add", "subtract", "set"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "变动类型不能为空")
    private String type;

    /**
     * 积分值
     * - 当type=add时：表示要增加的积分数量（例如：100表示增加100积分）
     * - 当type=subtract时：表示要扣除的积分数量（例如：50表示扣除50积分）
     * - 当type=set时：表示要设置的目标积分数量（例如：1000表示将积分设置为1000）
     */
    @Schema(description = "积分值（增加/扣除时为变动值，设置时为目标值）", 
            example = "100",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "积分值不能为空")
    private Integer points;

    /**
     * 备注/原因
     * 说明本次积分调整的原因，例如：
     * - "购买商品赠送"
     * - "活动奖励"
     * - "消费抵扣"
     * - "系统调整"
     * - "误操作补偿"
     */
    @Schema(description = "备注/原因（说明本次积分调整的原因）", 
            example = "购买商品赠送",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "备注不能为空")
    private String remark;

    /**
     * 关联商品ID（可选）
     * 如果本次积分变动是因为某个商品引起的，可以关联商品ID
     * 例如：购买商品赠送积分、积分兑换商品等场景
     * 映射到商品表的 id 字段（products.id）
     */
    @Schema(description = "关联商品ID（可选，如果本次积分变动与商品相关）", 
            example = "1",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("productId")
    private Long productId;
}
