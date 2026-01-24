package com.huakang.miniapp.controller;

import com.huakang.common.core.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 小程序端 - 上门服务订单Controller（预留接口）
 * 
 * <p><strong>功能说明：</strong></p>
 * <p>此接口为预留接口，用于未来可能在小程序中实现的"呼叫师傅上门服务"功能。</p>
 * <p>现阶段不实现具体逻辑，仅提供接口定义，方便二次开发时扩展。</p>
 * 
 * <p><strong>未来可能的功能：</strong></p>
 * <ul>
 *   <li>会员创建上门服务订单</li>
 *   <li>查看我的服务订单</li>
 *   <li>订单状态查询</li>
 *   <li>订单评价</li>
 * </ul>
 * 
 * @author huakang
 * @since 1.0.0
 */
@Slf4j
@Tag(name = "上门服务订单（预留）", description = "小程序端呼叫师傅上门服务功能，现阶段为预留接口，未实现具体逻辑")
@RestController
@RequestMapping("/service-orders")
@RequiredArgsConstructor
public class ServiceOrderController {

    @Operation(
        summary = "创建上门服务订单（预留）",
        description = "会员创建上门服务订单，现阶段未实现，返回提示信息"
    )
    @PostMapping
    public Result<String> createServiceOrder() {
        return Result.error("此功能为预留接口，暂未实现。如需使用，请联系开发团队进行二次开发。");
    }

    @Operation(
        summary = "查询我的服务订单（预留）",
        description = "查询当前会员的服务订单列表，现阶段未实现，返回提示信息"
    )
    @GetMapping("/my-orders")
    public Result<String> getMyServiceOrders(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.error("此功能为预留接口，暂未实现。如需使用，请联系开发团队进行二次开发。");
    }

    @Operation(
        summary = "查询订单详情（预留）",
        description = "查询服务订单详情，现阶段未实现，返回提示信息"
    )
    @GetMapping("/{id}")
    public Result<String> getServiceOrder(@PathVariable Long id) {
        return Result.error("此功能为预留接口，暂未实现。如需使用，请联系开发团队进行二次开发。");
    }

    @Operation(
        summary = "取消订单（预留）",
        description = "会员取消服务订单，现阶段未实现，返回提示信息"
    )
    @PostMapping("/{id}/cancel")
    public Result<String> cancelOrder(@PathVariable Long id) {
        return Result.error("此功能为预留接口，暂未实现。如需使用，请联系开发团队进行二次开发。");
    }

    @Operation(
        summary = "订单评价（预留）",
        description = "对已完成的服务订单进行评价，现阶段未实现，返回提示信息"
    )
    @PostMapping("/{id}/evaluate")
    public Result<String> evaluateOrder(
            @PathVariable Long id,
            @RequestParam Integer rating,
            @RequestParam(required = false) String comment) {
        return Result.error("此功能为预留接口，暂未实现。如需使用，请联系开发团队进行二次开发。");
    }
}
