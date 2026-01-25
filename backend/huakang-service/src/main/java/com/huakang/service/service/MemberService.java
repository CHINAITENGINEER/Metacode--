package com.huakang.service.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huakang.common.core.PageResult;
import com.huakang.mapper.entity.Member;
import com.huakang.service.dto.member.CreateMemberDTO;
import com.huakang.service.dto.member.MemberListDTO;
import com.huakang.service.dto.member.MemberVO;
import com.huakang.service.dto.member.PointsAdjustDTO;

/**
 * 会员服务接口
 *
 * @author huakang
 */
public interface MemberService {

    /**
     * 分页查询会员列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageResult<MemberVO> listMembers(MemberListDTO queryDTO);

    /**
     * 根据ID获取会员详情
     *
     * @param memberId 会员ID
     * @return 会员信息
     */
    MemberVO getMemberById(Long memberId);

    /**
     * 创建会员
     *
     * @param createDTO 创建信息
     * @param operatorId 操作人ID
     * @param operatorName 操作人姓名
     * @param operatorType 操作人类型
     * @return 会员信息
     */
    MemberVO createMember(CreateMemberDTO createDTO, Long operatorId, String operatorName, String operatorType);

    /**
     * 调整会员积分
     *
     * @param memberId 会员ID
     * @param adjustDTO 调整信息
     * @param operatorId 操作人ID
     * @param operatorName 操作人姓名
     * @param operatorType 操作人类型
     * @param ipAddress IP地址
     * @return 调整后的会员信息
     */
    MemberVO adjustPoints(Long memberId, PointsAdjustDTO adjustDTO, Long operatorId, String operatorName, String operatorType, String ipAddress);
}
