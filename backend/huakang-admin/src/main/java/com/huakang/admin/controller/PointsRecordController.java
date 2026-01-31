package com.huakang.admin.controller;

import com.huakang.common.core.PageResult;
import com.huakang.common.core.Result;
import com.huakang.service.annotation.RequireRole;
import com.huakang.service.dto.points.PointsRecordListDTO;
import com.huakang.service.dto.points.PointsRecordVO;
import com.huakang.service.service.PointsRecordService;
import com.huakang.service.utils.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 积分记录控制器
 *
 * @author huakang
 */
@Tag(name = "积分记录", description = "会员积分变动记录查询接口")
@RestController
@RequestMapping("/points-records")
@RequiredArgsConstructor
@RequireRole({"admin", "staff"})  // 管理员和店员都可以访问（数据权限在Service层控制）
public class PointsRecordController {

    private final PointsRecordService pointsRecordService;
    private final UserContext userContext;

    /**
     * 分页查询积分记录列表
     */
    @Operation(summary = "积分记录列表", description = "分页查询积分记录，支持按会员关键字（昵称/手机号）、操作人关键字（姓名）、时间范围筛选。店员只能查看自己的记录")
    @GetMapping("/list")
    public Result<PageResult<PointsRecordVO>> listRecords(PointsRecordListDTO queryDTO, HttpServletRequest request) {
        Long currentOperatorId = userContext.getCurrentUserId(request);
        String currentOperatorType = userContext.getCurrentUserRole(request);
        
        PageResult<PointsRecordVO> result = pointsRecordService.listRecords(queryDTO, currentOperatorId, currentOperatorType);
        return Result.success(result);
    }
}
