package com.huakang.service.schedule;

import com.huakang.service.dto.reconciliation.ReconciliationResultVO;
import com.huakang.service.service.PointsReconciliationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 积分对账定时任务
 * 每天凌晨2点执行积分对账检查
 *
 * @author huakang
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PointsReconciliationSchedule {

    private final PointsReconciliationService pointsReconciliationService;

    /**
     * 每天凌晨2点执行积分对账
     * cron表达式：秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void checkPointsConsistency() {
        log.info("开始执行积分对账任务...");
        
        try {
            List<ReconciliationResultVO> results = pointsReconciliationService.checkPointsConsistency();
            
            if (results.isEmpty()) {
                log.info("积分对账完成，所有会员积分一致");
            } else {
                log.error("积分对账发现 {} 个会员的积分不一致，详情：", results.size());
                for (ReconciliationResultVO result : results) {
                    log.error("会员ID: {}, 计算值: {}, 实际值: {}, 差异: {}", 
                            result.getMemberId(), 
                            result.getCalculatedPoints(), 
                            result.getActualPoints(), 
                            result.getDifference());
                }
                
                // TODO: 可以在这里添加告警逻辑，如发送邮件、短信等
                // sendAlert(results);
            }
        } catch (Exception e) {
            log.error("积分对账任务执行失败", e);
            // TODO: 可以在这里添加告警逻辑
            // sendErrorAlert(e);
        }
        
        log.info("积分对账任务执行完成");
    }
}
