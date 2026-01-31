package com.huakang.service.dto.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "数据大屏统计信息")
public class DashboardStatsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "会员总数", example = "1000")
    private Long totalMembers;

    @Schema(description = "累计发放积分总额（所有增加积分的总和）", example = "50000")
    private Long totalIssuedPoints;

    @Schema(description = "累计消耗积分总额（所有扣除积分的总和）", example = "30000")
    private Long totalConsumedPoints;

    @Schema(description = "当前积分池总额（所有会员当前积分的总和）", example = "20000")
    private Long currentPointsPool;
}
