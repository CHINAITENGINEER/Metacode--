package com.huakang.admin.controller;

import com.huakang.common.core.Result;
import com.huakang.service.annotation.RequireRole;
import com.huakang.service.service.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 导出Controller
 *
 * @author huakang
 */
@Slf4j
@Tag(name = "导出管理", description = "导出积分记录")
@RestController
@RequestMapping("/export")
@RequiredArgsConstructor
@RequireRole("admin")  // 整个Controller仅管理员可访问
public class ExportController {

    private final ExportService exportService;

    @Operation(summary = "导出积分记录", description = "导出积分变动记录，支持按时间范围、会员、操作人和变动类型筛选")
    @GetMapping("/points-records")
    public ResponseEntity<byte[]> exportPointsRecords(
            @Parameter(description = "开始时间（可选，不传则不限制起始时间）", example = "2025-01-01 00:00:00")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间（可选，不传则不限制结束时间）", example = "2025-01-31 23:59:59")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @Parameter(description = "会员关键字（可选，支持昵称或手机号模糊搜索）", example = "张三")
            @RequestParam(required = false) String memberKeyword,
            @Parameter(description = "操作人姓名（可选，支持模糊搜索，用于筛选特定操作人的记录）", example = "李四")
            @RequestParam(required = false) String operatorName,
            @Parameter(description = "变动类型（可选，精确匹配，如：购买商品、消费抵扣、系统调整等）", example = "购买商品")
            @RequestParam(required = false) String changeType) {

        byte[] excelBytes = exportService.exportPointsRecords(startTime, endTime, memberKeyword, operatorName, changeType);

        // 生成文件名
        String fileName;
        if (startTime != null && endTime != null) {
            fileName = String.format("积分记录_%s_%s.xlsx",
                    startTime.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                    endTime.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        } else if (startTime != null) {
            fileName = String.format("积分记录_%s起.xlsx",
                    startTime.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        } else if (endTime != null) {
            fileName = String.format("积分记录_至%s.xlsx",
                    endTime.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        } else {
            fileName = String.format("积分记录_%s.xlsx",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        }
        
        // 处理中文文件名编码
        try {
            fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString())
                    .replace("+", "%20");
        } catch (Exception e) {
            // 忽略编码错误
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelBytes);
    }
}
