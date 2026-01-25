package com.huakang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huakang.mapper.MemberMapper;
import com.huakang.mapper.PointsRecordMapper;
import com.huakang.mapper.entity.Member;
import com.huakang.mapper.entity.PointsRecord;
import com.huakang.service.dto.reconciliation.ReconciliationResultVO;
import com.huakang.service.dto.reconciliation.ReconciliationSummaryVO;
import com.huakang.service.service.PointsReconciliationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 积分对账服务实现类
 *
 * @author huakang
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PointsReconciliationServiceImpl implements PointsReconciliationService {

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;  // 可选，仅用于存储过程调用
    private final MemberMapper memberMapper;
    private final PointsRecordMapper pointsRecordMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ReconciliationResultVO> checkPointsConsistency() {
        List<ReconciliationResultVO> results = new ArrayList<>();
        
        // 查询所有未删除的会员
        LambdaQueryWrapper<Member> memberWrapper = new LambdaQueryWrapper<>();
        memberWrapper.eq(Member::getIsDeleted, 0);
        List<Member> members = memberMapper.selectList(memberWrapper);
        
        int inconsistentCount = 0;
        
        for (Member member : members) {
            // 计算积分记录中的积分总额
            Integer calculatedPoints = calculatePointsFromRecords(member.getId());
            
            // 获取会员表中的实际积分
            Integer actualPoints = member.getTotalPoints() != null ? member.getTotalPoints() : 0;
            
            // 检查是否一致
            if (!calculatedPoints.equals(actualPoints)) {
                ReconciliationResultVO result = ReconciliationResultVO.builder()
                        .memberId(member.getId())
                        .calculatedPoints(calculatedPoints)
                        .actualPoints(actualPoints)
                        .difference(calculatedPoints - actualPoints)
                        .status("INCONSISTENT")
                        .checkedAt(LocalDateTime.now())
                        .build();
                results.add(result);
                inconsistentCount++;
                
                log.warn("积分不一致 - 会员ID: {}, 计算值: {}, 实际值: {}, 差异: {}", 
                        member.getId(), calculatedPoints, actualPoints, calculatedPoints - actualPoints);
            }
        }
        
        if (inconsistentCount > 0) {
            log.error("积分对账发现 {} 个会员的积分不一致", inconsistentCount);
        } else {
            log.info("积分对账完成，所有会员积分一致");
        }
        
        return results;
    }

    @Override
    public ReconciliationSummaryVO getReconciliationSummary() {
        // 查询所有未删除的会员总数
        LambdaQueryWrapper<Member> memberWrapper = new LambdaQueryWrapper<>();
        memberWrapper.eq(Member::getIsDeleted, 0);
        long totalMembers = memberMapper.selectCount(memberWrapper);
        
        // 执行对账检查
        List<ReconciliationResultVO> results = checkPointsConsistency();
        long inconsistentCount = results.size();
        
        return ReconciliationSummaryVO.builder()
                .totalMembersChecked(totalMembers)
                .inconsistentCount(inconsistentCount)
                .checkedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 从积分记录计算会员的积分总额
     */
    private Integer calculatePointsFromRecords(Long memberId) {
        LambdaQueryWrapper<PointsRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PointsRecord::getMemberId, memberId);
        // 注意：points_records 表没有 is_deleted 字段，积分记录不可删除
        
        List<PointsRecord> records = pointsRecordMapper.selectList(wrapper);
        
        int totalPoints = 0;
        for (PointsRecord record : records) {
            String changeType = record.getChangeType();
            Integer points = record.getPoints() != null ? record.getPoints() : 0;
            
            // 根据变动类型计算积分
            if ("add".equals(changeType) || "consume_gift".equals(changeType)) {
                // 增加积分
                totalPoints += points;
            } else if ("subtract".equals(changeType) || "exchange".equals(changeType)) {
                // 扣除积分
                totalPoints -= points;
            }
        }
        
        return totalPoints;
    }

    /**
     * 使用存储过程执行对账（可选方法）
     * 注意：需要先执行 points_reconciliation_procedure.sql 创建存储过程
     */
    @SuppressWarnings("unused")
    private List<ReconciliationResultVO> checkPointsConsistencyByProcedure() {
        try {
            // 调用存储过程
            List<Map<String, Object>> resultList = jdbcTemplate.queryForList(
                    "CALL check_points_consistency()"
            );
            
            List<ReconciliationResultVO> results = new ArrayList<>();
            for (Map<String, Object> row : resultList) {
                ReconciliationResultVO result = ReconciliationResultVO.builder()
                        .memberId(((Number) row.get("member_id")).longValue())
                        .calculatedPoints(((Number) row.get("calculated_points")).intValue())
                        .actualPoints(((Number) row.get("actual_points")).intValue())
                        .difference(((Number) row.get("difference")).intValue())
                        .status((String) row.get("status"))
                        .checkedAt(LocalDateTime.now())
                        .build();
                results.add(result);
            }
            
            return results;
        } catch (Exception e) {
            log.error("调用存储过程执行对账失败，使用Java方式执行", e);
            // 如果存储过程不存在或执行失败，使用Java方式
            return checkPointsConsistency();
        }
    }
}
