package com.huakang.miniapp.controller;

import com.huakang.common.core.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试Controller
 *
 * @author huakang
 */
@Tag(name = "测试接口")
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    @Operation(summary = "健康检查")
    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("小程序API运行正常");
    }
}
