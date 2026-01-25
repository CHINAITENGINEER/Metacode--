package com.huakang.service.dto.points;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 积分记录VO
 *
 * @author huakang
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointsRecordVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    private Long id;

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 会员昵称
     */
    private String memberNickname;

    /**
     * 会员手机号
     */
    private String memberPhone;

    /**
     * 变动类型
     */
    private String changeType;

    /**
     * 变动分值
     */
    private Integer points;

    /**
     * 变动前积分余额
     */
    private Integer balanceBefore;

    /**
     * 变动后积分余额
     */
    private Integer balanceAfter;

    /**
     * 操作人类型
     */
    private String operatorType;

    /**
     * 操作人姓名
     */
    private String operatorName;

    /**
     * 变动时间
     */
    private LocalDateTime createdAt;

    /**
     * 备注
     */
    private String remark;
}
