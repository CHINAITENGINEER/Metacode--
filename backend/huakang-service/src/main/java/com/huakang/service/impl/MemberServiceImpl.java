package com.huakang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huakang.common.core.PageResult;
import com.huakang.common.exception.BusinessException;
import com.huakang.mapper.MemberMapper;
import com.huakang.mapper.PointsRecordMapper;
import com.huakang.mapper.entity.Member;
import com.huakang.mapper.entity.PointsRecord;
import com.huakang.service.dto.member.CreateMemberDTO;
import com.huakang.service.dto.member.MemberListDTO;
import com.huakang.service.dto.member.MemberVO;
import com.huakang.service.dto.member.PointsAdjustDTO;
import com.huakang.service.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 会员服务实现类
 *
 * @author huakang
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;
    private final PointsRecordMapper pointsRecordMapper;

    @Override
    public PageResult<MemberVO> listMembers(MemberListDTO queryDTO) {
        Page<Member> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        LambdaQueryWrapper<Member> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Member::getIsDeleted, 0);

        // 按昵称模糊查询
        if (queryDTO.getNickname() != null && !queryDTO.getNickname().trim().isEmpty()) {
            wrapper.like(Member::getNickname, queryDTO.getNickname().trim());
        }

        // 按手机号精确查询
        if (queryDTO.getPhone() != null && !queryDTO.getPhone().trim().isEmpty()) {
            wrapper.eq(Member::getPhone, queryDTO.getPhone().trim());
        }

        wrapper.orderByDesc(Member::getCreatedAt);
        Page<Member> result = memberMapper.selectPage(page, wrapper);

        // 转换为VO
        PageResult<MemberVO> pageResult = new PageResult<>();
        pageResult.setTotal(result.getTotal());
        pageResult.setPages(result.getPages());
        pageResult.setCurrent(result.getCurrent());
        pageResult.setSize(result.getSize());
        pageResult.setRecords(result.getRecords().stream()
                .map(this::convertToVO)
                .toList());

        return pageResult;
    }

    @Override
    public MemberVO getMemberById(Long memberId) {
        Member member = memberMapper.selectById(memberId);
        if (member == null || member.getIsDeleted() == 1) {
            throw new BusinessException("会员不存在");
        }
        return convertToVO(member);
    }

    @Override
    public MemberVO getMemberByOpenid(String openid) {
        LambdaQueryWrapper<Member> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Member::getOpenid, openid)
                .eq(Member::getIsDeleted, 0);
        Member member = memberMapper.selectOne(wrapper);
        if (member == null) {
            return null;
        }
        return convertToVO(member);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MemberVO createMember(CreateMemberDTO createDTO, Long operatorId, String operatorName, String operatorType) {
        // 检查手机号是否已存在
        if (createDTO.getPhone() != null && !createDTO.getPhone().trim().isEmpty()) {
            LambdaQueryWrapper<Member> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Member::getPhone, createDTO.getPhone().trim())
                    .eq(Member::getIsDeleted, 0);
            Long count = memberMapper.selectCount(wrapper);
            if (count > 0) {
                throw new BusinessException("该手机号已被注册");
            }
        }

        // 创建会员
        Member member = new Member();
        member.setOpenid(createDTO.getOpenid());
        member.setNickname(createDTO.getNickname());
        member.setAvatar(createDTO.getAvatar());
        member.setPhone(createDTO.getPhone());
        member.setTotalPoints(createDTO.getInitialPoints() != null ? createDTO.getInitialPoints() : 0);
        memberMapper.insert(member);

        // 如果有初始积分，创建积分记录
        if (createDTO.getInitialPoints() != null && createDTO.getInitialPoints() > 0) {
            PointsRecord record = new PointsRecord();
            record.setMemberId(member.getId());
            record.setChangeType("新会员注册");
            record.setPoints(createDTO.getInitialPoints());
            record.setBalanceBefore(0);
            record.setBalanceAfter(createDTO.getInitialPoints());
            record.setOperatorType(operatorType);
            record.setOperatorId(operatorId);
            record.setOperatorName(operatorName);
            record.setRemark("新会员注册赠送积分");
            pointsRecordMapper.insert(record);
        }

        return convertToVO(member);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MemberVO adjustPoints(Long memberId, PointsAdjustDTO adjustDTO, Long operatorId, String operatorName, String operatorType, String ipAddress) {
        // 查询会员
        Member member = memberMapper.selectById(memberId);
        if (member == null || member.getIsDeleted() == 1) {
            throw new BusinessException("会员不存在");
        }

        Integer balanceBefore = member.getTotalPoints() != null ? member.getTotalPoints() : 0;
        Integer balanceAfter;
        Integer points;
        String changeType;

        // 根据类型计算新积分
        switch (adjustDTO.getType()) {
            case "add":
                points = adjustDTO.getPoints();
                balanceAfter = balanceBefore + points;
                changeType = "后台调整";
                break;
            case "subtract":
                points = -adjustDTO.getPoints(); // 负数表示扣除
                balanceAfter = balanceBefore - adjustDTO.getPoints();
                if (balanceAfter < 0) {
                    throw new BusinessException("积分不足，无法扣除");
                }
                changeType = "积分扣除";
                break;
            case "set":
                points = adjustDTO.getPoints() - balanceBefore; // 计算差值
                balanceAfter = adjustDTO.getPoints();
                changeType = "后台调整";
                break;
            default:
                throw new BusinessException("无效的变动类型");
        }

        // 更新会员积分
        member.setTotalPoints(balanceAfter);
        memberMapper.updateById(member);

        // 创建积分记录
        PointsRecord record = new PointsRecord();
        record.setMemberId(memberId);
        record.setChangeType(changeType);
        record.setPoints(points);
        record.setBalanceBefore(balanceBefore);
        record.setBalanceAfter(balanceAfter);
        record.setOperatorType(operatorType);
        record.setOperatorId(operatorId);
        record.setOperatorName(operatorName);
        record.setRemark(adjustDTO.getRemark());
        record.setIpAddress(ipAddress);
        pointsRecordMapper.insert(record);

        return convertToVO(member);
    }

    /**
     * 转换为VO
     */
    private MemberVO convertToVO(Member member) {
        MemberVO vo = new MemberVO();
        BeanUtils.copyProperties(member, vo);
        return vo;
    }
}
