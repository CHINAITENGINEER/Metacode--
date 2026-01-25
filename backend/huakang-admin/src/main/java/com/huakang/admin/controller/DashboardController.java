package com.huakang.admin.controller;

import com.huakang.common.core.Result;
import com.huakang.service.annotation.RequireRole;
import com.huakang.service.dto.dashboard.DashboardStatsVO;
import com.huakang.service.dto.dashboard.TrendDataVO;
import com.huakang.service.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 数据大屏控制器
 *
 * @author huakang
 */
@Tag(name = "数据大屏", description = "数据大屏统计和趋势图表接口")
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@RequireRole("admin")  // 整个Controller仅管理员可访问
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * 获取核心指标统计
     */
    @Operation(summary = "获取核心指标统计", description = "获取会员总数、积分统计等核心指标")
    @GetMapping("/stats")
    public Result<DashboardStatsVO> getStats() {
        DashboardStatsVO stats = dashboardService.getStats();
        return Result.success(stats);
    }

    /**
     * 获取近30天趋势数据
     */
    @Operation(summary = "获取趋势数据", description = "获取近30天新注册用户和积分发放/消耗趋势")
    @GetMapping("/trends")
    public Result<List<TrendDataVO>> getTrends() {
        List<TrendDataVO> trendData = dashboardService.getTrendData();
        return Result.success(trendData);
    }
}
