package com.huakang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huakang.mapper.entity.Member;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员Mapper接口
 *
 * @author huakang
 */
@Mapper
public interface MemberMapper extends BaseMapper<Member> {
}
