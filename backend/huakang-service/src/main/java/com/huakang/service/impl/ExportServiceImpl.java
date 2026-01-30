package com.huakang.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huakang.common.exception.BusinessException;
import com.huakang.mapper.entity.Member;
import com.huakang.mapper.entity.PointsRecord;
import com.huakang.mapper.MemberMapper;
import com.huakang.mapper.PointsRecordMapper;
import com.huakang.service.dto.export.PointsRecordExportDTO;
import com.huakang.service.service.ExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 导出服务实现
 *
 * @author huakang
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {

    private final PointsRecordMapper pointsRecordMapper;
    private final MemberMapper memberMapper;

    @Override
    public byte[] exportPointsRecords(LocalDateTime startTime, LocalDateTime endTime, Long memberId) {
        try {
            // 1. 查询积分记录
            LambdaQueryWrapper<PointsRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.ge(PointsRecord::getCreatedAt, startTime)
                   .le(PointsRecord::getCreatedAt, endTime)
                   .orderByDesc(PointsRecord::getCreatedAt);
            
            if (memberId != null) {
                wrapper.eq(PointsRecord::getMemberId, memberId);
            }

            List<PointsRecord> records = pointsRecordMapper.selectList(wrapper);

            // 2. 转换为导出DTO
            List<PointsRecordExportDTO> exportList = records.stream().map(record -> {
                PointsRecordExportDTO dto = new PointsRecordExportDTO();
                dto.setId(record.getId());
                dto.setChangeType(record.getChangeType());
                dto.setPoints(record.getPoints());
                dto.setBalanceBefore(record.getBalanceBefore());
                dto.setBalanceAfter(record.getBalanceAfter());
                dto.setOperatorType(record.getOperatorType());
                dto.setOperatorName(record.getOperatorName());
                dto.setBusinessNo(record.getBusinessNo());
                dto.setRemark(record.getRemark());
                dto.setCreatedAt(record.getCreatedAt());

                // 查询会员信息
                Member member = memberMapper.selectById(record.getMemberId());
                if (member != null) {
                    dto.setMemberNickname(member.getNickname());
                    dto.setMemberPhone(member.getPhone());
                }

                return dto;
            }).collect(Collectors.toList());

            // 3. 导出Excel
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            EasyExcel.write(outputStream, PointsRecordExportDTO.class)
                    .sheet("积分记录")
                    .doWrite(exportList);

            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("导出积分记录失败", e);
            throw new BusinessException("导出积分记录失败：" + e.getMessage());
        }
    }
}
