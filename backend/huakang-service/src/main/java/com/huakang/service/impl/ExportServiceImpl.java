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
    public byte[] exportPointsRecords(LocalDateTime startTime, LocalDateTime endTime, String memberKeyword, String operatorName, String changeType) {
        try {
            // 1. 构建查询条件
            LambdaQueryWrapper<PointsRecord> wrapper = new LambdaQueryWrapper<>();
            
            // 时间范围（可选）
            if (startTime != null) {
                wrapper.ge(PointsRecord::getCreatedAt, startTime);
            }
            if (endTime != null) {
                wrapper.le(PointsRecord::getCreatedAt, endTime);
            }
            
            // 会员关键字（可选，支持昵称或手机号模糊搜索）
            if (memberKeyword != null && !memberKeyword.trim().isEmpty()) {
                String keyword = memberKeyword.trim();
                // 先查询符合条件的会员ID列表
                LambdaQueryWrapper<Member> memberWrapper = new LambdaQueryWrapper<>();
                memberWrapper.and(w -> w.like(Member::getNickname, keyword)
                                        .or()
                                        .like(Member::getPhone, keyword));
                List<Member> members = memberMapper.selectList(memberWrapper);
                
                if (members.isEmpty()) {
                    // 如果没有匹配的会员，返回空结果
                    wrapper.eq(PointsRecord::getMemberId, -1L);
                } else {
                    // 使用会员ID列表进行查询
                    List<Long> memberIds = members.stream()
                            .map(Member::getId)
                            .collect(Collectors.toList());
                    wrapper.in(PointsRecord::getMemberId, memberIds);
                }
            }
            
            // 操作人姓名（可选，模糊搜索）
            if (operatorName != null && !operatorName.trim().isEmpty()) {
                wrapper.like(PointsRecord::getOperatorName, operatorName.trim());
            }
            
            // 变动类型（可选，精确匹配）
            if (changeType != null && !changeType.trim().isEmpty()) {
                wrapper.eq(PointsRecord::getChangeType, changeType.trim());
            }
            
            // 按创建时间倒序排列
            wrapper.orderByDesc(PointsRecord::getCreatedAt);

            // 2. 查询积分记录
            List<PointsRecord> records = pointsRecordMapper.selectList(wrapper);

            // 3. 转换为导出DTO
            List<PointsRecordExportDTO> exportList = records.stream().map(record -> {
                PointsRecordExportDTO dto = new PointsRecordExportDTO();
                
                // 基本信息
                dto.setId(record.getId());
                dto.setChangeType(record.getChangeType());
                dto.setPoints(record.getPoints());
                dto.setBalanceBefore(record.getBalanceBefore());
                dto.setBalanceAfter(record.getBalanceAfter());
                
                // 操作人信息
                dto.setOperatorType(record.getOperatorType());
                dto.setOperatorName(record.getOperatorName());
                
                // 业务信息
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

            // 4. 导出Excel
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
