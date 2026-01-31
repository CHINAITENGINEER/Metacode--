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
     * @param startTime       开始时间（可选，不传则不限制起始时间）
     * @param endTime         结束时间（可选，不传则不限制结束时间）
     * @param memberKeyword   会员关键字（可选，支持昵称或手机号模糊搜索）
     * @param operatorName    操作人姓名（可选，支持模糊搜索）
     * @param changeType      变动类型（可选，精确匹配）
     * @return Excel文件字节数组
     */
    byte[] exportPointsRecords(LocalDateTime startTime, LocalDateTime endTime, String memberKeyword, String operatorName, String changeType);
}
