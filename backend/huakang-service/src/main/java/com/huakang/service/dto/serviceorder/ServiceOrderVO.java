package com.huakang.service.dto.serviceorder;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 上门服务订单视图对象（预留）
 * 
 * @author huakang
 * @since 1.0.0
 */
@Data
public class ServiceOrderVO {

    /**
     * 订单ID
     */
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 会员昵称
     */
    private String memberNickname;

    /**
     * 会员手机号
     */
    private String memberPhone;

    /**
     * 服务类型
     */
    private String serviceType;

    /**
     * 服务地址
     */
    private String address;

    /**
     * 详细地址
     */
    private String detailAddress;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 预约服务时间
     */
    private LocalDateTime serviceTime;

    /**
     * 订单状态
     * 待接单、已接单、进行中、已完成、已取消
     */
    private String status;

    /**
     * 师傅ID
     */
    private Long masterId;

    /**
     * 师傅姓名
     */
    private String masterName;

    /**
     * 师傅电话
     */
    private String masterPhone;

    /**
     * 服务费用（元）
     */
    private BigDecimal serviceFee;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
