package com.huakang.service.service;

/**
 * 上门服务订单服务接口（预留）
 * 
 * <p>功能说明：</p>
 * <p>未来可能实现的功能：</p>
 * <ul>
 *   <li>创建上门服务订单</li>
 *   <li>查询服务订单列表</li>
 *   <li>更新订单状态（待接单、进行中、已完成、已取消）</li>
 *   <li>师傅接单</li>
 *   <li>订单评价</li>
 * </ul>
 * 
 * <p>现阶段：接口预留，不实现具体逻辑</p>
 * 
 * @author huakang
 * @since 1.0.0
 */
public interface ServiceOrderService {

    /**
     * 创建上门服务订单（预留）
     * 
     * @param memberId 会员ID
     * @param serviceType 服务类型（安装、维修、清洗等）
     * @param address 服务地址
     * @param contactPhone 联系电话
     * @param serviceTime 预约服务时间
     * @param remark 备注
     * @return 订单ID
     */
    // Long createServiceOrder(Long memberId, String serviceType, String address, 
    //                        String contactPhone, LocalDateTime serviceTime, String remark);

    /**
     * 查询服务订单列表（预留）
     * 
     * @param memberId 会员ID（可选）
     * @param status 订单状态（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param page 页码
     * @param size 每页大小
     * @return 订单列表
     */
    // PageResult<ServiceOrderVO> listServiceOrders(Long memberId, String status, 
    //                                               LocalDateTime startTime, LocalDateTime endTime, 
    //                                               Integer page, Integer size);

    /**
     * 师傅接单（预留）
     * 
     * @param orderId 订单ID
     * @param masterId 师傅ID
     * @return 是否成功
     */
    // Boolean acceptOrder(Long orderId, Long masterId);

    /**
     * 更新订单状态（预留）
     * 
     * @param orderId 订单ID
     * @param status 新状态
     * @return 是否成功
     */
    // Boolean updateOrderStatus(Long orderId, String status);
}
