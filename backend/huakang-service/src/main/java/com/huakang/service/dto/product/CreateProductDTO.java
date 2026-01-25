package com.huakang.service.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 创建商品DTO
 *
 * @author huakang
 */
@Data
public class CreateProductDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品名称
     */
    @NotBlank(message = "商品名称不能为空")
    private String name;

    /**
     * 商品主图URL
     */
    private String image;

    /**
     * 商品详情图URL列表
     */
    private List<String> detailImages;

    /**
     * 商品分类
     */
    private String category;

    /**
     * 商品类型：1=全量商品，2=积分商品
     */
    @NotNull(message = "商品类型不能为空")
    private Integer type;

    /**
     * 商品价格（元）
     */
    @Positive(message = "商品价格必须大于0")
    private BigDecimal price;

    /**
     * 积分价格（积分商品必填）
     */
    private Integer pointsPrice;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 状态：1=上架，0=下架
     */
    private Integer status = 1;

    /**
     * 排序权重
     */
    private Integer sortOrder = 0;
}
