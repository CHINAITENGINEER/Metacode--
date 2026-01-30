package com.huakang.miniapp.controller;

import com.huakang.common.core.Result;
import com.huakang.service.dto.auth.WechatLoginDTO;
import com.huakang.service.dto.auth.WechatLoginVO;
import com.huakang.service.service.WechatAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 小程序认证控制器
 *
 * @author huakang
 */
@Slf4j
@Tag(name = "小程序认证", description = "微信登录、用户认证相关接口")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class WechatAuthController {

    private final WechatAuthService wechatAuthService;

    @Operation(summary = "微信登录", description = "通过微信授权码换取用户信息和访问令牌")
    @PostMapping("/wechat-login")
    public Result<WechatLoginVO> wechatLogin(@RequestBody WechatLoginDTO loginDTO) {
        WechatLoginVO result = wechatAuthService.wechatLogin(loginDTO);
        return Result.success(result);
    }
}