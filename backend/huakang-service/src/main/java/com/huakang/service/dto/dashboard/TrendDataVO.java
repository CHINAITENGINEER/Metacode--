package com.huakang.service.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 趋势数据VO
 *
 * @author huakang
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrendDataVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日期
     */
    private LocalDate date;

    /**
     * 新注册用户数
     */
    private Long newMembers;

    /**
     * 发放积分
     */
    private Long issuedPoints;

    /**
     * 消耗积分
     */
    private Long consumedPoints;
}
