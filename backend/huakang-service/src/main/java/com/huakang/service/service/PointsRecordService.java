package com.huakang.service.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huakang.common.core.PageResult;
import com.huakang.service.dto.points.PointsRecordListDTO;
import com.huakang.service.dto.points.PointsRecordVO;

/**
 * 积分记录服务接口
 *
 * @author huakang
 */
public interface PointsRecordService {

    /**
     * 分页查询积分记录列表
     *
     * @param queryDTO 查询条件
     * @param currentOperatorId 当前操作人ID（用于店员只能查看自己的记录）
     * @param currentOperatorType 当前操作人类型
     * @return 分页结果
     */
    PageResult<PointsRecordVO> listRecords(PointsRecordListDTO queryDTO, Long currentOperatorId, String currentOperatorType);
}
