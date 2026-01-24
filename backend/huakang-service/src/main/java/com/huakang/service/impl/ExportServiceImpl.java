package com.huakang.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huakang.common.exception.BusinessException;
import com.huakang.mapper.entity.Member;
import com.huakang.mapper.entity.PointsRecord;
import com.huakang.mapper.mapper.MemberMapper;
import com.huakang.mapper.mapper.PointsRecordMapper;
import com.huakang.mapper.mapper.SystemConfigMapper;
import com.huakang.mapper.entity.SystemConfig;
import com.huakang.service.dto.export.ConsumptionRecordExportDTO;
import com.huakang.service.dto.export.FinancialReportDTO;
import com.huakang.service.dto.export.PointsRecordExportDTO;
import com.huakang.service.service.ExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final SystemConfigMapper systemConfigMapper;

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

    @Override
    public byte[] exportConsumptionRecords(LocalDateTime startTime, LocalDateTime endTime, Long memberId) {
        try {
            // 1. 查询消费赠送类型的积分记录（对应真钱消费）
            LambdaQueryWrapper<PointsRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PointsRecord::getChangeType, "消费赠送")
                   .ge(PointsRecord::getCreatedAt, startTime)
                   .le(PointsRecord::getCreatedAt, endTime)
                   .orderByDesc(PointsRecord::getCreatedAt);

            if (memberId != null) {
                wrapper.eq(PointsRecord::getMemberId, memberId);
            }

            List<PointsRecord> records = pointsRecordMapper.selectList(wrapper);

            // 2. 转换为消费记录DTO
            // 注意：这里需要根据业务规则计算消费金额
            // 假设：1元 = 1积分（可根据系统配置调整）
            List<ConsumptionRecordExportDTO> exportList = records.stream().map(record -> {
                ConsumptionRecordExportDTO dto = new ConsumptionRecordExportDTO();
                dto.setId(record.getId());
                dto.setPoints(record.getPoints());
                dto.setBusinessNo(record.getBusinessNo());
                dto.setOperatorName(record.getOperatorName());
                dto.setRemark(record.getRemark());
                dto.setCreatedAt(record.getCreatedAt());

                // 计算消费金额（根据积分比例）
                BigDecimal consumptionRatio = getConsumptionRatio();
                // 消费金额 = 赠送积分 / 积分比例（例如：100积分 / 0.01 = 10000元）
                BigDecimal amount = new BigDecimal(record.getPoints()).divide(consumptionRatio, 2, BigDecimal.ROUND_HALF_UP);
                dto.setAmount(amount);

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
            EasyExcel.write(outputStream, ConsumptionRecordExportDTO.class)
                    .sheet("真钱消费记录")
                    .doWrite(exportList);

            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("导出真钱消费记录失败", e);
            throw new BusinessException("导出真钱消费记录失败：" + e.getMessage());
        }
    }

    @Override
    public byte[] exportFinancialReport(LocalDateTime startTime, LocalDateTime endTime) {
        try {
            // 1. 查询所有积分记录
            LambdaQueryWrapper<PointsRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.ge(PointsRecord::getCreatedAt, startTime)
                   .le(PointsRecord::getCreatedAt, endTime);

            List<PointsRecord> records = pointsRecordMapper.selectList(wrapper);

            // 2. 统计各项数据
            List<FinancialReportDTO> reportList = new ArrayList<>();

            // 2.1 真钱消费统计
            BigDecimal totalConsumption = BigDecimal.ZERO;
            Integer totalConsumptionPoints = 0;
            BigDecimal consumptionRatio = getConsumptionRatio();
            
            long consumptionCount = records.stream()
                    .filter(r -> "消费赠送".equals(r.getChangeType()))
                    .count();
            for (PointsRecord record : records) {
                if ("消费赠送".equals(record.getChangeType())) {
                    // 消费金额 = 赠送积分 / 积分比例
                    BigDecimal amount = new BigDecimal(record.getPoints()).divide(consumptionRatio, 2, BigDecimal.ROUND_HALF_UP);
                    totalConsumption = totalConsumption.add(amount);
                    totalConsumptionPoints += record.getPoints();
                }
            }

            FinancialReportDTO consumptionReport = new FinancialReportDTO();
            consumptionReport.setItem("真钱消费总额");
            consumptionReport.setAmount(totalConsumption);
            consumptionReport.setPoints(totalConsumptionPoints);
            consumptionReport.setCount(consumptionCount);
            consumptionReport.setRemark("消费赠送积分对应的真钱消费");
            reportList.add(consumptionReport);

            // 2.2 积分发放统计
            Integer totalIssued = records.stream()
                    .filter(r -> r.getPoints() > 0)
                    .mapToInt(PointsRecord::getPoints)
                    .sum();
            long issuedCount = records.stream()
                    .filter(r -> r.getPoints() > 0)
                    .count();

            FinancialReportDTO issuedReport = new FinancialReportDTO();
            issuedReport.setItem("累计发放积分");
            issuedReport.setAmount(BigDecimal.ZERO);
            issuedReport.setPoints(totalIssued);
            issuedReport.setCount(issuedCount);
            issuedReport.setRemark("所有增加积分的记录");
            reportList.add(issuedReport);

            // 2.3 积分消耗统计
            Integer totalConsumed = records.stream()
                    .filter(r -> r.getPoints() < 0)
                    .mapToInt(r -> Math.abs(r.getPoints()))
                    .sum();
            long consumedCount = records.stream()
                    .filter(r -> r.getPoints() < 0)
                    .count();

            FinancialReportDTO consumedReport = new FinancialReportDTO();
            consumedReport.setItem("累计消耗积分");
            consumedReport.setAmount(BigDecimal.ZERO);
            consumedReport.setPoints(totalConsumed);
            consumedReport.setCount(consumedCount);
            consumedReport.setRemark("所有扣除积分的记录");
            reportList.add(consumedReport);

            // 2.4 积分兑换统计
            BigDecimal totalExchangeAmount = BigDecimal.ZERO;
            Integer totalExchangePoints = 0;
            long exchangeCount = 0;

            for (PointsRecord record : records) {
                if ("积分兑换".equals(record.getChangeType())) {
                    totalExchangePoints += Math.abs(record.getPoints());
                    exchangeCount++;
                    // 积分兑换的商品价值（可根据商品表计算）
                    // TODO: 如果关联了商品，可以从商品表查询价格
                }
            }

            FinancialReportDTO exchangeReport = new FinancialReportDTO();
            exchangeReport.setItem("积分兑换总额");
            exchangeReport.setAmount(totalExchangeAmount);
            exchangeReport.setPoints(totalExchangePoints);
            exchangeReport.setCount(exchangeCount);
            exchangeReport.setRemark("积分兑换商品对应的价值");
            reportList.add(exchangeReport);

            // 3. 导出Excel
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            EasyExcel.write(outputStream, FinancialReportDTO.class)
                    .sheet("财务统计报表")
                    .doWrite(reportList);

            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("导出财务统计报表失败", e);
            throw new BusinessException("导出财务统计报表失败：" + e.getMessage());
        }
    }

    /**
     * 获取消费积分比例
     * 从系统配置读取，默认：0.01（1元=0.01积分，即100元=1积分）
     */
    private BigDecimal getConsumptionRatio() {
        try {
            SystemConfig config = systemConfigMapper.selectOne(
                new LambdaQueryWrapper<SystemConfig>()
                    .eq(SystemConfig::getConfigKey, "points_consumption_ratio")
            );
            if (config != null && config.getConfigValue() != null) {
                return new BigDecimal(config.getConfigValue());
            }
        } catch (Exception e) {
            log.warn("读取积分比例配置失败，使用默认值", e);
        }
        // 默认值：0.01（1元=0.01积分，即100元=1积分）
        return new BigDecimal("0.01");
    }
}
