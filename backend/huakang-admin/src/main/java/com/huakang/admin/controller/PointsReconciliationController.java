package com.huakang.admin.controller;

import com.huakang.common.core.Result;
import com.huakang.service.annotation.RequireRole;
import com.huakang.service.dto.reconciliation.ReconciliationResultVO;
import com.huakang.service.dto.reconciliation.ReconciliationSummaryVO;
import com.huakang.service.service.PointsReconciliationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 积分对账控制器
 *
 * @author huakang
 */
@Tag(name = "积分对账", description = "积分对账检查接口")
@RestController
@RequestMapping("/reconciliation")
@RequiredArgsConstructor
@RequireRole("admin")  // 仅管理员可访问
public class PointsReconciliationController {

    private final PointsReconciliationService pointsReconciliationService;

    /**
     * 执行积分对账
     */
    @Operation(summary = "执行对账", description = "手动执行积分对账检查，返回不一致的记录")
    @GetMapping("/check")
    public Result<List<ReconciliationResultVO>> checkPointsConsistency() {
        List<ReconciliationResultVO> results = pointsReconciliationService.checkPointsConsistency();
        return Result.success(results);
    }

    /**
     * 获取对账汇总
     */
    @Operation(summary = "对账汇总", description = "获取积分对账汇总信息")
    @GetMapping("/summary")
    public Result<ReconciliationSummaryVO> getReconciliationSummary() {
        ReconciliationSummaryVO summary = pointsReconciliationService.getReconciliationSummary();
        return Result.success(summary);
    }
}
