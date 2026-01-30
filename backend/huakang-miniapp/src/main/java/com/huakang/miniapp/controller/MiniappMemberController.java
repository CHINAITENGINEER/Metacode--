package com.huakang.miniapp.controller;

import com.huakang.common.core.Result;
import com.huakang.service.dto.member.MemberVO;
import com.huakang.service.service.MemberService;
import com.huakang.service.utils.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 小程序会员控制器
 *
 * @author huakang
 */
@Slf4j
@Tag(name = "小程序会员", description = "会员信息管理接口")
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MiniappMemberController {

    private final MemberService memberService;
    private final UserContext userContext;

    @Operation(summary = "获取会员信息", description = "获取当前登录会员的详细信息，包含积分总额")
    @GetMapping("/profile")
    public Result<MemberVO> getProfile(HttpServletRequest request) {
        // 从小程序JWT Token中获取用户ID
        Long userId = userContext.getCurrentUserId(request);
        if (userId == null) {
            return Result.error("请先登录");
        }

        MemberVO member = memberService.getMemberById(userId);
        return Result.success(member);
    }
}