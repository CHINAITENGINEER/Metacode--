package com.huakang.service.service;

import com.huakang.service.dto.reconciliation.ReconciliationResultVO;
import com.huakang.service.dto.reconciliation.ReconciliationSummaryVO;

import java.util.List;

/**
 * 积分对账服务接口
 *
 * @author huakang
 */
public interface PointsReconciliationService {

    /**
     * 执行积分对账
     * 检查所有会员的积分总额与积分记录是否一致
     *
     * @return 对账结果列表
     */
    List<ReconciliationResultVO> checkPointsConsistency();

    /**
     * 获取对账汇总信息
     *
     * @return 对账汇总
     */
    ReconciliationSummaryVO getReconciliationSummary();
}
