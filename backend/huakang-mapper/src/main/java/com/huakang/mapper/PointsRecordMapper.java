package com.huakang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huakang.mapper.entity.PointsRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 积分记录Mapper接口
 *
 * @author huakang
 */
@Mapper
public interface PointsRecordMapper extends BaseMapper<PointsRecord> {

    /**
     * 统计累计发放积分总额（points > 0）
     * 使用SQL聚合，避免全表查询到内存
     */
    @Select("SELECT COALESCE(SUM(points), 0) as total FROM points_records WHERE points > 0")
    Long sumIssuedPoints();

    /**
     * 统计累计消耗积分总额（points < 0）
     * 使用SQL聚合，避免全表查询到内存
     */
    @Select("SELECT COALESCE(SUM(ABS(points)), 0) as total FROM points_records WHERE points < 0")
    Long sumConsumedPoints();

    /**
     * 按日期分组统计积分发放和消耗（近30天）
     * 使用SQL GROUP BY，避免在Java内存中分组聚合
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 日期、发放积分、消耗积分的统计结果
     */
    @Select("SELECT " +
            "DATE(created_at) as date, " +
            "COALESCE(SUM(CASE WHEN points > 0 THEN points ELSE 0 END), 0) as issued_points, " +
            "COALESCE(SUM(CASE WHEN points < 0 THEN ABS(points) ELSE 0 END), 0) as consumed_points " +
            "FROM points_records " +
            "WHERE created_at >= #{startDate} AND created_at <= #{endDate} " +
            "GROUP BY DATE(created_at) " +
            "ORDER BY date")
    java.util.List<Map<String, Object>> getPointsTrendByDate(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
