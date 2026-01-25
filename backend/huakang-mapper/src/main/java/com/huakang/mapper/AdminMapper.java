package com.huakang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huakang.mapper.entity.Admin;
import org.apache.ibatis.annotations.Mapper;

/**
 * 管理员Mapper接口
 *
 * @author huakang
 */
@Mapper
public interface AdminMapper extends BaseMapper<Admin> {
}
