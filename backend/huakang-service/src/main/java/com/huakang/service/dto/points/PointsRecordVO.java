package com.huakang.service.dto.points;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 积分记录VO
 *
 * @author huakang
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "积分记录信息")
public class PointsRecordVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "记录ID", example = "1")
    private Long id;

    @Schema(description = "会员ID", example = "1")
    private Long memberId;

    @Schema(description = "会员昵称", example = "张三")
    private String memberNickname;

    @Schema(description = "会员手机号", example = "13800138000")
    private String memberPhone;

    @Schema(description = "变动类型（如：购买商品、消费抵扣、系统调整等）", example = "购买商品")
    private String changeType;

    @Schema(description = "变动分值（正数表示增加，负数表示扣除）", example = "100")
    private Integer points;

    @Schema(description = "变动前积分余额", example = "500")
    private Integer balanceBefore;

    @Schema(description = "变动后积分余额", example = "600")
    private Integer balanceAfter;

    @Schema(description = "操作人类型（admin=管理员，staff=店员，system=系统）", example = "staff")
    private String operatorType;

    @Schema(description = "操作人姓名", example = "李四")
    private String operatorName;

    @Schema(description = "变动时间", example = "2025-01-01T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "备注说明", example = "购买商品赠送")
    private String remark;

    @Schema(description = "关联商品ID", example = "1")
    private Long productId;

    @Schema(description = "关联商品名称（从products表的name字段获取）", example = "海尔冰箱")
    private String productName;
}
