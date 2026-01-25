package com.huakang.mapper.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 积分记录实体类
 *
 * @author huakang
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("points_records")
public class PointsRecord {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 会员ID
     */
    @TableField("member_id")
    private Long memberId;

    /**
     * 变动类型：消费赠送/积分兑换/后台调整/新会员注册/积分扣除/积分过期
     */
    @TableField("change_type")
    private String changeType;

    /**
     * 变动分值（正数=增加，负数=扣除）
     */
    @TableField("points")
    private Integer points;

    /**
     * 变动前积分余额
     */
    @TableField("balance_before")
    private Integer balanceBefore;

    /**
     * 变动后积分余额
     */
    @TableField("balance_after")
    private Integer balanceAfter;

    /**
     * 业务单号（如订单号、兑换单号，便于对账）
     */
    @TableField("business_no")
    private String businessNo;

    /**
     * 关联商品ID（积分兑换时）
     */
    @TableField("product_id")
    private Long productId;

    /**
     * 操作人类型：admin/staff/system
     */
    @TableField("operator_type")
    private String operatorType;

    /**
     * 操作人ID（管理员或店员ID）
     */
    @TableField("operator_id")
    private Long operatorId;

    /**
     * 操作人姓名（冗余字段，便于查询）
     */
    @TableField("operator_name")
    private String operatorName;

    /**
     * 备注/原因
     */
    @TableField("remark")
    private String remark;

    /**
     * 操作IP地址
     */
    @TableField("ip_address")
    private String ipAddress;

    /**
     * 用户代理（设备信息）
     */
    @TableField("user_agent")
    private String userAgent;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
