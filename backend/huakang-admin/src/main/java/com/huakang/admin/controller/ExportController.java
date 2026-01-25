package com.huakang.admin.controller;

import com.huakang.common.core.Result;
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
@Tag(name = "导出管理", description = "导出积分记录、消费记录、财务报表")
@RestController
@RequestMapping("/export")
@RequiredArgsConstructor
public class ExportController {

    private final ExportService exportService;

    @Operation(summary = "导出积分记录", description = "导出指定时间范围内的积分变动记录")
    @GetMapping("/points-records")
    public ResponseEntity<byte[]> exportPointsRecords(
            @Parameter(description = "开始时间", example = "2025-01-01 00:00:00")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间", example = "2025-01-31 23:59:59")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @Parameter(description = "会员ID（可选）")
            @RequestParam(required = false) Long memberId) {

        byte[] excelBytes = exportService.exportPointsRecords(startTime, endTime, memberId);

        String fileName = String.format("积分记录_%s_%s.xlsx",
                startTime.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                endTime.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        
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

    @Operation(summary = "导出真钱消费记录", description = "导出指定时间范围内的真钱消费记录（消费赠送积分对应的消费）")
    @GetMapping("/consumption-records")
    public ResponseEntity<byte[]> exportConsumptionRecords(
            @Parameter(description = "开始时间", example = "2025-01-01 00:00:00")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间", example = "2025-01-31 23:59:59")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @Parameter(description = "会员ID（可选）")
            @RequestParam(required = false) Long memberId) {

        byte[] excelBytes = exportService.exportConsumptionRecords(startTime, endTime, memberId);

        String fileName = String.format("真钱消费记录_%s_%s.xlsx",
                startTime.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                endTime.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        
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

    @Operation(summary = "导出财务统计报表", description = "导出财务统计报表（包含真钱消费、积分发放、积分消耗等汇总）")
    @GetMapping("/financial-report")
    public ResponseEntity<byte[]> exportFinancialReport(
            @Parameter(description = "开始时间", example = "2025-01-01 00:00:00")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间", example = "2025-01-31 23:59:59")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {

        byte[] excelBytes = exportService.exportFinancialReport(startTime, endTime);

        String fileName = String.format("财务统计报表_%s_%s.xlsx",
                startTime.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                endTime.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        
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
