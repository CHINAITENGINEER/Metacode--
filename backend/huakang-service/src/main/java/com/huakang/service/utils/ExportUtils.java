package com.huakang.service.utils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

/**
 * 导出工具类
 *
 * @author huakang
 */
public class ExportUtils {

    /**
     * 格式化文件名（处理中文编码）
     */
    public static String formatFileName(String prefix, String startDate, String endDate) {
        String fileName = String.format("%s_%s_%s.xlsx", prefix, startDate, endDate);
        try {
            return URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString())
                    .replace("+", "%20");
        } catch (Exception e) {
            return fileName;
        }
    }

    /**
     * 日期格式化器
     */
    public static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyyMMdd");
}
