package com.huakang.service.dto.reconciliation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 积分对账结果VO
 *
 * @author huakang
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReconciliationResultVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 计算出的积分总额（从积分记录计算）
     */
    private Integer calculatedPoints;

    /**
     * 实际积分总额（会员表中的值）
     */
    private Integer actualPoints;

    /**
     * 差异值（计算值 - 实际值）
     */
    private Integer difference;

    /**
     * 状态：CONSISTENT=一致，INCONSISTENT=不一致
     */
    private String status;

    /**
     * 检查时间
     */
    private LocalDateTime checkedAt;
}
