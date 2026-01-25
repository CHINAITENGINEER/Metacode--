package com.huakang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huakang.common.core.PageResult;
import com.huakang.mapper.MemberMapper;
import com.huakang.mapper.PointsRecordMapper;
import com.huakang.mapper.entity.Member;
import com.huakang.mapper.entity.PointsRecord;
import com.huakang.service.dto.points.PointsRecordListDTO;
import com.huakang.service.dto.points.PointsRecordVO;
import com.huakang.service.service.PointsRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 积分记录服务实现类
 *
 * @author huakang
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PointsRecordServiceImpl implements PointsRecordService {

    private final PointsRecordMapper pointsRecordMapper;
    private final MemberMapper memberMapper;

    @Override
    public PageResult<PointsRecordVO> listRecords(PointsRecordListDTO queryDTO, Long currentOperatorId, String currentOperatorType) {
        Page<PointsRecord> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        LambdaQueryWrapper<PointsRecord> wrapper = new LambdaQueryWrapper<>();

        // 店员只能查看自己操作的记录
        if ("staff".equals(currentOperatorType)) {
            wrapper.eq(PointsRecord::getOperatorType, "staff")
                    .eq(PointsRecord::getOperatorId, currentOperatorId);
        }

        // 按会员ID筛选
        if (queryDTO.getMemberId() != null) {
            wrapper.eq(PointsRecord::getMemberId, queryDTO.getMemberId());
        }

        // 按操作人类型筛选
        if (queryDTO.getOperatorType() != null && !queryDTO.getOperatorType().trim().isEmpty()) {
            wrapper.eq(PointsRecord::getOperatorType, queryDTO.getOperatorType().trim());
        }

        // 按操作人ID筛选
        if (queryDTO.getOperatorId() != null) {
            wrapper.eq(PointsRecord::getOperatorId, queryDTO.getOperatorId());
        }

        // 按时间范围筛选
        if (queryDTO.getStartTime() != null) {
            wrapper.ge(PointsRecord::getCreatedAt, queryDTO.getStartTime());
        }
        if (queryDTO.getEndTime() != null) {
            wrapper.le(PointsRecord::getCreatedAt, queryDTO.getEndTime());
        }

        wrapper.orderByDesc(PointsRecord::getCreatedAt);
        Page<PointsRecord> result = pointsRecordMapper.selectPage(page, wrapper);

        // 批量查询会员信息
        List<Long> memberIds = result.getRecords().stream()
                .map(PointsRecord::getMemberId)
                .distinct()
                .toList();
        
        Map<Long, Member> memberMap;
        if (memberIds.isEmpty()) {
            memberMap = Map.of();
        } else {
            LambdaQueryWrapper<Member> memberWrapper = new LambdaQueryWrapper<>();
            memberWrapper.in(Member::getId, memberIds);
            List<Member> members = memberMapper.selectList(memberWrapper);
            memberMap = members.stream()
                    .collect(Collectors.toMap(Member::getId, member -> member));
        }

        // 转换为VO
        PageResult<PointsRecordVO> pageResult = new PageResult<>();
        pageResult.setTotal(result.getTotal());
        pageResult.setPages(result.getPages());
        pageResult.setCurrent(result.getCurrent());
        pageResult.setSize(result.getSize());
        pageResult.setRecords(result.getRecords().stream()
                .map(record -> convertToVO(record, memberMap.get(record.getMemberId())))
                .toList());

        return pageResult;
    }

    /**
     * 转换为VO
     */
    private PointsRecordVO convertToVO(PointsRecord record, Member member) {
        PointsRecordVO vo = PointsRecordVO.builder()
                .id(record.getId())
                .memberId(record.getMemberId())
                .memberNickname(member != null ? member.getNickname() : null)
                .memberPhone(member != null ? member.getPhone() : null)
                .changeType(record.getChangeType())
                .points(record.getPoints())
                .balanceBefore(record.getBalanceBefore())
                .balanceAfter(record.getBalanceAfter())
                .operatorType(record.getOperatorType())
                .operatorName(record.getOperatorName())
                .createdAt(record.getCreatedAt())
                .remark(record.getRemark())
                .build();
        return vo;
    }
}
