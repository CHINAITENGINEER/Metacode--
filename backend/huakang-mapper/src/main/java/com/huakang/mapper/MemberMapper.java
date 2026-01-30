package com.huakang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huakang.mapper.entity.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 会员Mapper接口
 *
 * @author huakang
 */
@Mapper
public interface MemberMapper extends BaseMapper<Member> {

    /**
     * 统计当前积分池总额（所有会员积分总和）
     * 使用SQL聚合，避免全表查询到内存
     */
    @Select("SELECT COALESCE(SUM(total_points), 0) as total FROM members WHERE is_deleted = 0")
    Long sumTotalPoints();

    /**
     * 按日期分组统计新注册用户数（近30天）
     * 使用SQL GROUP BY，避免在Java内存中分组聚合
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 日期、新注册用户数的统计结果
     */
    @Select("SELECT " +
            "DATE(created_at) as date, " +
            "COUNT(*) as count " +
            "FROM members " +
            "WHERE is_deleted = 0 AND created_at >= #{startDate} AND created_at <= #{endDate} " +
            "GROUP BY DATE(created_at) " +
            "ORDER BY date")
    java.util.List<Map<String, Object>> getNewMembersTrendByDate(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
