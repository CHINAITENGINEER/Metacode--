package com.huakang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huakang.mapper.MemberMapper;
import com.huakang.mapper.PointsRecordMapper;
import com.huakang.mapper.entity.Member;
import com.huakang.service.dto.dashboard.DashboardStatsVO;
import com.huakang.service.dto.dashboard.TrendDataVO;
import com.huakang.service.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据大屏服务实现类
 *
 * @author huakang
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final MemberMapper memberMapper;
    private final PointsRecordMapper pointsRecordMapper;

    /**
     * 获取数据大屏统计指标
     * 
     * 性能优化：
     * 1. 使用SQL聚合函数（SUM、COUNT）替代Java内存聚合，避免全表查询导致OOM
     * 2. 添加Redis缓存，5分钟过期，减少数据库查询压力
     * 
     * 优化前：查询所有记录到内存，在Java中求和（数据量大时可能OOM）
     * 优化后：使用SQL聚合，只返回聚合结果（单行数据）
     */
    @Override
    @Cacheable(value = "dashboard_stats", key = "'stats'", unless = "#result == null")
    public DashboardStatsVO getStats() {
        // 1. 会员总数 - 使用COUNT聚合，只返回数量
        Long totalMembers = memberMapper.selectCount(
                new LambdaQueryWrapper<Member>()
                        .eq(Member::getIsDeleted, 0)
        );

        // 2. 累计发放积分总额（points > 0）- 使用SQL SUM聚合
        Long totalIssuedPoints = pointsRecordMapper.sumIssuedPoints();
        if (totalIssuedPoints == null) {
            totalIssuedPoints = 0L;
        }

        // 3. 累计消耗积分总额（points < 0）- 使用SQL SUM聚合
        Long totalConsumedPoints = pointsRecordMapper.sumConsumedPoints();
        if (totalConsumedPoints == null) {
            totalConsumedPoints = 0L;
        }

        // 4. 当前积分池总额（所有会员积分总和）- 使用SQL SUM聚合
        Long currentPointsPool = memberMapper.sumTotalPoints();
        if (currentPointsPool == null) {
            currentPointsPool = 0L;
        }

        return DashboardStatsVO.builder()
                .totalMembers(totalMembers)
                .totalIssuedPoints(totalIssuedPoints)
                .totalConsumedPoints(totalConsumedPoints)
                .currentPointsPool(currentPointsPool)
                .build();
    }

    /**
     * 获取数据大屏趋势数据（近30天）
     * 
     * 性能优化：
     * 1. 使用SQL GROUP BY聚合替代Java内存分组，减少数据传输和内存占用
     * 2. 添加Redis缓存，10分钟过期，减少数据库查询压力
     * 
     * 优化前：查询所有记录到内存，在Java中按日期分组和求和（数据量大时可能OOM）
     * 优化后：使用SQL GROUP BY，数据库直接返回按日期聚合的结果
     */
    @Override
    @Cacheable(value = "dashboard_trend", key = "'trend'", unless = "#result == null || #result.isEmpty()")
    public List<TrendDataVO> getTrendData() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(29); // 近30天
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        // 1. 使用SQL GROUP BY统计近30天新注册用户（按日期分组）
        List<Map<String, Object>> newMembersTrend = memberMapper.getNewMembersTrendByDate(startDateTime, endDateTime);
        Map<LocalDate, Long> newMembersByDate = newMembersTrend.stream()
                .collect(Collectors.toMap(
                        map -> {
                            // 处理日期转换（可能是Date或String类型）
                            Object dateObj = map.get("date");
                            if (dateObj instanceof LocalDate) {
                                return (LocalDate) dateObj;
                            } else if (dateObj instanceof java.sql.Date) {
                                return ((java.sql.Date) dateObj).toLocalDate();
                            } else if (dateObj instanceof String) {
                                return LocalDate.parse((String) dateObj);
                            } else {
                                return LocalDate.parse(dateObj.toString());
                            }
                        },
                        map -> {
                            Object countObj = map.get("count");
                            if (countObj instanceof Long) {
                                return (Long) countObj;
                            } else if (countObj instanceof Integer) {
                                return ((Integer) countObj).longValue();
                            } else if (countObj instanceof BigDecimal) {
                                return ((BigDecimal) countObj).longValue();
                            } else {
                                return Long.parseLong(countObj.toString());
                            }
                        }
                ));

        // 2. 使用SQL GROUP BY统计近30天积分发放和消耗（按日期分组）
        List<Map<String, Object>> pointsTrend = pointsRecordMapper.getPointsTrendByDate(startDateTime, endDateTime);
        Map<LocalDate, PointsTrendData> pointsByDate = pointsTrend.stream()
                .collect(Collectors.toMap(
                        map -> {
                            Object dateObj = map.get("date");
                            if (dateObj instanceof LocalDate) {
                                return (LocalDate) dateObj;
                            } else if (dateObj instanceof java.sql.Date) {
                                return ((java.sql.Date) dateObj).toLocalDate();
                            } else if (dateObj instanceof String) {
                                return LocalDate.parse((String) dateObj);
                            } else {
                                return LocalDate.parse(dateObj.toString());
                            }
                        },
                        map -> {
                            Object issuedObj = map.get("issued_points");
                            Object consumedObj = map.get("consumed_points");
                            
                            Long issued = convertToLong(issuedObj);
                            Long consumed = convertToLong(consumedObj);
                            
                            return new PointsTrendData(issued, consumed);
                        }
                ));

        // 3. 生成30天的趋势数据（补齐缺失的日期）
        List<TrendDataVO> trendDataList = new ArrayList<>();
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            Long newMembersCount = newMembersByDate.getOrDefault(currentDate, 0L);
            PointsTrendData pointsData = pointsByDate.getOrDefault(currentDate, new PointsTrendData(0L, 0L));

            trendDataList.add(TrendDataVO.builder()
                    .date(currentDate)
                    .newMembers(newMembersCount)
                    .issuedPoints(pointsData.issuedPoints)
                    .consumedPoints(pointsData.consumedPoints)
                    .build());

            currentDate = currentDate.plusDays(1);
        }

        return trendDataList;
    }

    /**
     * 将对象转换为Long类型
     */
    private Long convertToLong(Object obj) {
        if (obj == null) {
            return 0L;
        }
        if (obj instanceof Long) {
            return (Long) obj;
        } else if (obj instanceof Integer) {
            return ((Integer) obj).longValue();
        } else if (obj instanceof BigDecimal) {
            return ((BigDecimal) obj).longValue();
        } else {
            return Long.parseLong(obj.toString());
        }
    }

    /**
     * 积分趋势数据内部类
     */
    private static class PointsTrendData {
        final Long issuedPoints;
        final Long consumedPoints;

        PointsTrendData(Long issuedPoints, Long consumedPoints) {
            this.issuedPoints = issuedPoints != null ? issuedPoints : 0L;
            this.consumedPoints = consumedPoints != null ? consumedPoints : 0L;
        }
    }
}
