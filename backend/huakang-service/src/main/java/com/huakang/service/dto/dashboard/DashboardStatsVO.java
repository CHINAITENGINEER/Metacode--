package com.huakang.service.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 数据大屏统计VO
 *
 * @author huakang
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 会员总数
     */
    private Long totalMembers;

    /**
     * 累计发放积分总额
     */
    private Long totalIssuedPoints;

    /**
     * 累计消耗积分总额
     */
    private Long totalConsumedPoints;

    /**
     * 当前积分池总额（剩余积分）
     */
    private Long currentPointsPool;
}
