package com.huakang.service.dto.export;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 财务统计报表DTO
 *
 * @author huakang
 */
@Data
public class FinancialReportDTO {

    @ExcelProperty(value = "统计项", index = 0)
    private String item;

    @ExcelProperty(value = "金额（元）", index = 1)
    private BigDecimal amount;

    @ExcelProperty(value = "积分", index = 2)
    private Integer points;

    @ExcelProperty(value = "笔数", index = 3)
    private Long count;

    @ExcelProperty(value = "备注", index = 4)
    private String remark;
}
