package com.huakang.service.dto.points;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 积分记录列表查询DTO
 *
 * @author huakang
 */
@Data
@Schema(description = "积分记录列表查询条件")
public class PointsRecordListDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 页码（从1开始）
     */
    @Schema(description = "页码", example = "1")
    private Integer page = 1;

    /**
     * 每页大小
     */
    @Schema(description = "每页大小", example = "10")
    private Integer size = 10;

    /**
     * 会员ID（精确查询）
     */
    @Schema(description = "会员ID（可选，用于查询指定会员的积分记录）", example = "1")
    private Long memberId;

    /**
     * 会员关键字（昵称/手机号）
     */
    @Schema(description = "会员关键字（支持昵称/手机号模糊搜索）", example = "张三")
    private String memberKeyword;

    /**
     * 操作人关键字（姓名）
     */
    @Schema(description = "操作人关键字（支持姓名模糊搜索）", example = "李四")
    private String operatorKeyword;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间", example = "2025-01-01T00:00:00")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间", example = "2025-01-31T23:59:59")
    private LocalDateTime endTime;
}
