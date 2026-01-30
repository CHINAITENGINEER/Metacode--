package com.huakang.service.service;

import java.time.LocalDateTime;

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
}
