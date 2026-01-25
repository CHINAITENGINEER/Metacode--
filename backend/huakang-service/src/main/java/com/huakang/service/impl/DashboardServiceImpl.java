package com.huakang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huakang.mapper.MemberMapper;
import com.huakang.mapper.PointsRecordMapper;
import com.huakang.mapper.entity.Member;
import com.huakang.mapper.entity.PointsRecord;
import com.huakang.service.dto.dashboard.DashboardStatsVO;
import com.huakang.service.dto.dashboard.TrendDataVO;
import com.huakang.service.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @Override
    public DashboardStatsVO getStats() {
        // 1. 会员总数
        Long totalMembers = memberMapper.selectCount(
                new LambdaQueryWrapper<Member>()
                        .eq(Member::getIsDeleted, 0)
        );

        // 2. 累计发放积分总额（points > 0）
        List<PointsRecord> issuedRecords = pointsRecordMapper.selectList(
                new LambdaQueryWrapper<PointsRecord>()
                        .gt(PointsRecord::getPoints, 0)
        );
        Long totalIssuedPoints = issuedRecords.stream()
                .mapToLong(record -> record.getPoints().longValue())
                .sum();

        // 3. 累计消耗积分总额（points < 0）
        List<PointsRecord> consumedRecords = pointsRecordMapper.selectList(
                new LambdaQueryWrapper<PointsRecord>()
                        .lt(PointsRecord::getPoints, 0)
        );
        Long totalConsumedPoints = consumedRecords.stream()
                .mapToLong(record -> Math.abs(record.getPoints().longValue()))
                .sum();

        // 4. 当前积分池总额（所有会员积分总和）
        List<Member> members = memberMapper.selectList(
                new LambdaQueryWrapper<Member>()
                        .eq(Member::getIsDeleted, 0)
        );
        Long currentPointsPool = members.stream()
                .mapToLong(member -> member.getTotalPoints() != null ? member.getTotalPoints().longValue() : 0L)
                .sum();

        return DashboardStatsVO.builder()
                .totalMembers(totalMembers)
                .totalIssuedPoints(totalIssuedPoints)
                .totalConsumedPoints(totalConsumedPoints)
                .currentPointsPool(currentPointsPool)
                .build();
    }

    @Override
    public List<TrendDataVO> getTrendData() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(29); // 近30天

        // 1. 查询近30天新注册用户
        List<Member> newMembers = memberMapper.selectList(
                new LambdaQueryWrapper<Member>()
                        .eq(Member::getIsDeleted, 0)
                        .ge(Member::getCreatedAt, startDate.atStartOfDay())
                        .le(Member::getCreatedAt, endDate.atTime(23, 59, 59))
        );

        // 按日期分组统计新注册用户数
        Map<LocalDate, Long> newMembersByDate = newMembers.stream()
                .collect(Collectors.groupingBy(
                        member -> member.getCreatedAt().toLocalDate(),
                        Collectors.counting()
                ));

        // 2. 查询近30天积分记录
        List<PointsRecord> pointsRecords = pointsRecordMapper.selectList(
                new LambdaQueryWrapper<PointsRecord>()
                        .ge(PointsRecord::getCreatedAt, startDate.atStartOfDay())
                        .le(PointsRecord::getCreatedAt, endDate.atTime(23, 59, 59))
        );

        // 按日期分组统计积分发放和消耗
        Map<LocalDate, List<PointsRecord>> recordsByDate = pointsRecords.stream()
                .collect(Collectors.groupingBy(
                        record -> record.getCreatedAt().toLocalDate()
                ));

        // 3. 生成30天的趋势数据
        List<TrendDataVO> trendDataList = new ArrayList<>();
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            Long newMembersCount = newMembersByDate.getOrDefault(currentDate, 0L);

            List<PointsRecord> dayRecords = recordsByDate.getOrDefault(currentDate, new ArrayList<>());
            Long issuedPoints = dayRecords.stream()
                    .filter(record -> record.getPoints() > 0)
                    .mapToLong(record -> record.getPoints().longValue())
                    .sum();
            Long consumedPoints = dayRecords.stream()
                    .filter(record -> record.getPoints() < 0)
                    .mapToLong(record -> Math.abs(record.getPoints().longValue()))
                    .sum();

            trendDataList.add(TrendDataVO.builder()
                    .date(currentDate)
                    .newMembers(newMembersCount)
                    .issuedPoints(issuedPoints)
                    .consumedPoints(consumedPoints)
                    .build());

            currentDate = currentDate.plusDays(1);
        }

        return trendDataList;
    }
}
