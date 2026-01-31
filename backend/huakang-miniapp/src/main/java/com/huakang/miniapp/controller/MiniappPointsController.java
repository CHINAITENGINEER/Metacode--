package com.huakang.miniapp.controller;

import com.huakang.common.core.PageResult;
import com.huakang.common.core.Result;
import com.huakang.service.dto.points.PointsRecordListDTO;
import com.huakang.service.dto.points.PointsRecordVO;
import com.huakang.service.service.PointsRecordService;
import com.huakang.service.utils.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 小程序积分控制器
 *
 * @author huakang
 */
@Slf4j
@Tag(name = "小程序积分", description = "积分查询、兑换接口")
@RestController
@RequestMapping("/points-records")
@RequiredArgsConstructor
public class MiniappPointsController {

    private final PointsRecordService pointsRecordService;
    private final UserContext userContext;

    @Operation(summary = "积分记录列表", description = "获取当前会员的积分记录，以列表形式展示积分的变动历史")
    @GetMapping
    public Result<PageResult<PointsRecordVO>> listPointsRecords(PointsRecordListDTO queryDTO, HttpServletRequest request) {
        // 从小程序JWT Token中获取用户ID
        Long userId = userContext.getCurrentUserId(request);
        if (userId == null) {
            return Result.error("请先登录");
        }

        // 小程序端：会员只能查看自己的积分记录
        // 通过在Service层传入userId和userType="member"来实现数据权限控制
        PageResult<PointsRecordVO> result = pointsRecordService.listRecords(queryDTO, userId, "member");
        return Result.success(result);
    }
}