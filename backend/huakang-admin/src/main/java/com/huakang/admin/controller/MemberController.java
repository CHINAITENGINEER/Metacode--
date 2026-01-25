package com.huakang.admin.controller;

import com.huakang.common.core.PageResult;
import com.huakang.common.core.Result;
import com.huakang.service.annotation.RequireRole;
import com.huakang.service.dto.member.CreateMemberDTO;
import com.huakang.service.dto.member.MemberListDTO;
import com.huakang.service.dto.member.MemberVO;
import com.huakang.service.dto.member.PointsAdjustDTO;
import com.huakang.service.service.MemberService;
import com.huakang.service.utils.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 会员管理控制器
 *
 * @author huakang
 */
@Tag(name = "会员管理", description = "会员列表、创建、积分操作接口")
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
@RequireRole({"admin", "staff"})  // 管理员和店员都可以访问
public class MemberController {

    private final MemberService memberService;
    private final UserContext userContext;

    /**
     * 获取客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 分页查询会员列表
     */
    @Operation(summary = "会员列表", description = "分页查询会员列表，支持按昵称、手机号查询")
    @GetMapping("/list")
    public Result<PageResult<MemberVO>> listMembers(MemberListDTO queryDTO) {
        PageResult<MemberVO> result = memberService.listMembers(queryDTO);
        return Result.success(result);
    }

    /**
     * 获取会员详情
     */
    @Operation(summary = "会员详情", description = "根据ID获取会员详细信息")
    @GetMapping("/{id}")
    public Result<MemberVO> getMember(@PathVariable Long id) {
        MemberVO member = memberService.getMemberById(id);
        return Result.success(member);
    }

    /**
     * 创建会员
     */
    @Operation(summary = "创建会员", description = "创建新会员（主要用于线下录入）")
    @PostMapping
    public Result<MemberVO> createMember(@Valid @RequestBody CreateMemberDTO createDTO, HttpServletRequest request) {
        Long operatorId = userContext.getCurrentUserId(request);
        String operatorName = userContext.getCurrentUsername(request);
        String operatorType = userContext.getCurrentUserRole(request);
        
        MemberVO member = memberService.createMember(createDTO, operatorId, operatorName, operatorType);
        return Result.success("创建成功", member);
    }

    /**
     * 调整会员积分
     */
    @Operation(summary = "调整积分", description = "对指定会员进行积分增加、扣除或直接设置")
    @PostMapping("/{id}/points/adjust")
    public Result<MemberVO> adjustPoints(
            @PathVariable Long id,
            @Valid @RequestBody PointsAdjustDTO adjustDTO,
            HttpServletRequest request) {
        Long operatorId = userContext.getCurrentUserId(request);
        String operatorName = userContext.getCurrentUsername(request);
        String operatorType = userContext.getCurrentUserRole(request);
        String ipAddress = getClientIp(request);

        MemberVO member = memberService.adjustPoints(id, adjustDTO, operatorId, operatorName, operatorType, ipAddress);
        return Result.success("积分调整成功", member);
    }
}
