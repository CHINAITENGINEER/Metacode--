package com.huakang.service.dto.export;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 积分记录导出DTO
 *
 * @author huakang
 */
@Data
public class PointsRecordExportDTO {

    @ExcelProperty(value = "记录ID", index = 0)
    private Long id;

    @ExcelProperty(value = "会员昵称", index = 1)
    private String memberNickname;

    @ExcelProperty(value = "会员手机号", index = 2)
    private String memberPhone;

    @ExcelProperty(value = "变动类型", index = 3)
    private String changeType;

    @ExcelProperty(value = "变动分值", index = 4)
    private Integer points;

    @ExcelProperty(value = "变动前余额", index = 5)
    private Integer balanceBefore;

    @ExcelProperty(value = "变动后余额", index = 6)
    private Integer balanceAfter;

    @ExcelProperty(value = "操作人类型", index = 7)
    private String operatorType;

    @ExcelProperty(value = "操作人姓名", index = 8)
    private String operatorName;

    @ExcelProperty(value = "业务单号", index = 9)
    private String businessNo;

    @ExcelProperty(value = "备注", index = 10)
    private String remark;

    @ExcelProperty(value = "操作时间", index = 11)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
