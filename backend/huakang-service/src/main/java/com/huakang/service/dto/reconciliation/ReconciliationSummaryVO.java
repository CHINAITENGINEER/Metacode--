package com.huakang.service.dto.reconciliation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 积分对账汇总VO
 *
 * @author huakang
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReconciliationSummaryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 检查的会员总数
     */
    private Long totalMembersChecked;

    /**
     * 不一致的会员数量
     */
    private Long inconsistentCount;

    /**
     * 检查时间
     */
    private LocalDateTime checkedAt;
}
