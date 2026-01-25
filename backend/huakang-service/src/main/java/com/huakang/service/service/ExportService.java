package com.huakang.service.service;

import com.huakang.common.core.PageResult;
import com.huakang.service.dto.export.PointsRecordExportDTO;
import com.huakang.service.dto.export.ConsumptionRecordExportDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 导出服务接口
 *
 * @author huakang
 */
public interface ExportService {

    /**
     * 导出积分记录
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param memberId  会员ID（可选）
     * @return Excel文件字节数组
     */
    byte[] exportPointsRecords(LocalDateTime startTime, LocalDateTime endTime, Long memberId);

    /**
     * 导出真钱消费记录（消费赠送积分对应的消费记录）
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param memberId  会员ID（可选）
     * @return Excel文件字节数组
     */
    byte[] exportConsumptionRecords(LocalDateTime startTime, LocalDateTime endTime, Long memberId);

    /**
     * 导出财务统计报表（积分+真钱消费汇总）
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return Excel文件字节数组
     */
    byte[] exportFinancialReport(LocalDateTime startTime, LocalDateTime endTime);
}
