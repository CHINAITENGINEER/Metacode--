package com.huakang.service.dto.export;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 真钱消费记录导出DTO
 *
 * @author huakang
 */
@Data
public class ConsumptionRecordExportDTO {

    @ExcelProperty(value = "记录ID", index = 0)
    private Long id;

    @ExcelProperty(value = "会员昵称", index = 1)
    private String memberNickname;

    @ExcelProperty(value = "会员手机号", index = 2)
    private String memberPhone;

    @ExcelProperty(value = "消费金额（元）", index = 3)
    private BigDecimal amount;

    @ExcelProperty(value = "赠送积分", index = 4)
    private Integer points;

    @ExcelProperty(value = "业务单号", index = 5)
    private String businessNo;

    @ExcelProperty(value = "操作人", index = 6)
    private String operatorName;

    @ExcelProperty(value = "备注", index = 7)
    private String remark;

    @ExcelProperty(value = "消费时间", index = 8)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
