package com.huakang.service.service;

import com.huakang.service.dto.dashboard.DashboardStatsVO;
import com.huakang.service.dto.dashboard.TrendDataVO;

import java.util.List;

/**
 * 数据大屏服务接口
 *
 * @author huakang
 */
public interface DashboardService {

    /**
     * 获取核心指标统计
     *
     * @return 统计数据
     */
    DashboardStatsVO getStats();

    /**
     * 获取近30天趋势数据
     *
     * @return 趋势数据列表
     */
    List<TrendDataVO> getTrendData();
}
