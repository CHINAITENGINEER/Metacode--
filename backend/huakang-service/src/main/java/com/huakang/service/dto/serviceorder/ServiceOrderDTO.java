package com.huakang.service.dto.serviceorder;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 上门服务订单DTO（预留）
 * 
 * <p>用于创建上门服务订单的请求参数</p>
 * 
 * @author huakang
 * @since 1.0.0
 */
@Data
public class ServiceOrderDTO {

    /**
     * 会员ID
     */
    @NotNull(message = "会员ID不能为空")
    private Long memberId;

    /**
     * 服务类型
     * 例如：安装、维修、清洗、保养等
     */
    @NotBlank(message = "服务类型不能为空")
    private String serviceType;

    /**
     * 服务地址
     */
    @NotBlank(message = "服务地址不能为空")
    private String address;

    /**
     * 详细地址
     */
    private String detailAddress;

    /**
     * 联系电话
     */
    @NotBlank(message = "联系电话不能为空")
    private String contactPhone;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 预约服务时间
     */
    @NotNull(message = "预约服务时间不能为空")
    private LocalDateTime serviceTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 商品ID（如果服务与商品相关）
     */
    private Long productId;
}
