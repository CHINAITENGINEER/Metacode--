package com.huakang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huakang.mapper.entity.Staff;
import org.apache.ibatis.annotations.Mapper;

/**
 * 店员Mapper接口
 *
 * @author huakang
 */
@Mapper
public interface StaffMapper extends BaseMapper<Staff> {
}
