package com.huakang.service.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品VO
 *
 * @author huakang
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品ID
     */
    private Long id;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品主图URL
     */
    private String image;

    /**
     * 商品详情图（List<String>）
     */
    private List<String> detailImages;

    /**
     * 商品分类
     */
    private String category;

    /**
     * 商品类型：1=全量商品，2=积分商品
     */
    private Integer type;

    /**
     * 商品价格（元）
     */
    private BigDecimal price;

    /**
     * 积分价格
     */
    private Integer pointsPrice;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 状态：1=上架，0=下架
     */
    private Integer status;

    /**
     * 排序权重
     */
    private Integer sortOrder;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
