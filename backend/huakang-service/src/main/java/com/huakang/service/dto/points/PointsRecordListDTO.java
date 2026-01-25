package com.huakang.service.dto.points;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 积分记录列表查询DTO
 *
 * @author huakang
 */
@Data
public class PointsRecordListDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 页码（从1开始）
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 操作人类型：admin/staff/system
     */
    private String operatorType;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;
}
