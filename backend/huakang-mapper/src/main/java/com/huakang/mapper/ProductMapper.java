package com.huakang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huakang.mapper.entity.Product;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品Mapper接口
 *
 * @author huakang
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}
