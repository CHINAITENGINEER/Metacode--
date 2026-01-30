package com.huakang.admin.controller;

import com.huakang.common.core.Result;
import com.huakang.service.annotation.RequireRole;
import com.huakang.service.dto.system.SystemConfigVO;
import com.huakang.service.dto.system.UpdateSystemConfigDTO;
import com.huakang.service.service.OssService;
import com.huakang.service.service.SystemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 系统设置控制器
 *
 * @author huakang
 */
@Tag(name = "系统设置", description = "系统配置管理、企业微信二维码上传接口")
@RestController
@RequestMapping("/system")
@RequiredArgsConstructor
@RequireRole("admin")  // 仅管理员可访问
public class SystemConfigController {

    private final SystemConfigService systemConfigService;
    private final OssService ossService;

    /**
     * 获取所有系统配置
     */
    @Operation(summary = "获取所有配置", description = "获取所有系统配置")
    @GetMapping("/configs")
    public Result<List<SystemConfigVO>> getAllConfigs() {
        List<SystemConfigVO> configs = systemConfigService.getAllConfigs();
        return Result.success(configs);
    }

    /**
     * 根据配置键获取配置
     */
    @Operation(summary = "获取配置", description = "根据配置键获取配置值")
    @GetMapping("/configs/{configKey}")
    public Result<SystemConfigVO> getConfig(@PathVariable String configKey) {
        SystemConfigVO config = systemConfigService.getConfigByKey(configKey);
        return Result.success(config);
    }

    /**
     * 根据配置分组获取配置列表
     */
    @Operation(summary = "按分组获取配置", description = "根据配置分组获取配置列表")
    @GetMapping("/configs/group/{configGroup}")
    public Result<List<SystemConfigVO>> getConfigsByGroup(@PathVariable String configGroup) {
        List<SystemConfigVO> configs = systemConfigService.getConfigsByGroup(configGroup);
        return Result.success(configs);
    }

    /**
     * 更新系统配置
     */
    @Operation(summary = "更新配置", description = "更新指定配置键的配置值")
    @PostMapping("/configs/{configKey}")
    public Result<SystemConfigVO> updateConfig(
            @PathVariable String configKey,
            @Valid @RequestBody UpdateSystemConfigDTO updateDTO) {
        SystemConfigVO config = systemConfigService.updateConfig(configKey, updateDTO);
        return Result.success("更新成功", config);
    }

    /**
     * 上传企业微信二维码
     */
    @Operation(summary = "上传二维码", description = "上传企业微信二维码到OSS")
    @PostMapping("/wechat-qrcode")
    public Result<SystemConfigVO> uploadWechatQrcode(@RequestParam("file") MultipartFile file) {
        // 上传图片到OSS
        String imageUrl = ossService.uploadImage(file, "qrcode");
        // 保存到系统配置
        SystemConfigVO config = systemConfigService.uploadWechatQrcode(imageUrl);
        return Result.success("上传成功", config);
    }

    /**
     * 图片上传接口
     */
    @Operation(summary = "图片上传", description = "上传图片到OSS，返回图片URL")
    @PostMapping("/upload")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        String imageUrl = ossService.uploadImage(file, "common");
        return Result.success("上传成功", imageUrl);
    }
}
