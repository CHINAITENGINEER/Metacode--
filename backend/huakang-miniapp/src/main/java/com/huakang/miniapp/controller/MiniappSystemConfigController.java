package com.huakang.miniapp.controller;

import com.huakang.common.core.Result;
import com.huakang.service.dto.system.SystemConfigVO;
import com.huakang.service.service.SystemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 小程序系统配置控制器
 *
 * @author huakang
 */
@Slf4j
@Tag(name = "系统配置", description = "系统配置相关接口")
@RestController
@RequestMapping("/system-configs")
@RequiredArgsConstructor
public class MiniappSystemConfigController {

    private final SystemConfigService systemConfigService;

    @Operation(summary = "获取企业微信二维码", description = "获取用于\"点击咨询\"功能的企业微信二维码")
    @GetMapping("/wechat-qrcode")
    public Result<String> getWechatQrCode() {
        SystemConfigVO config = systemConfigService.getConfigByKey("wechat_qrcode_url");
        if (config == null || config.getConfigValue() == null || config.getConfigValue().isEmpty()) {
            return Result.success(""); // 返回空字符串，前端需要处理
        }
        return Result.success(config.getConfigValue());
    }
}