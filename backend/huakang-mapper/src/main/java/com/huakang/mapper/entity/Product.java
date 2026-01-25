package com.huakang.mapper.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体类
 *
 * @author huakang
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("products")
public class Product {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商品名称
     */
    @TableField("name")
    private String name;

    /**
     * 商品主图URL
     */
    @TableField("image")
    private String image;

    /**
     * 商品详情图（JSON数组）
     * 注意：MySQL 8.0的JSON类型，MyBatis-Plus会自动处理
     */
    @TableField(value = "detail_images")
    private String detailImages;

    /**
     * 商品分类
     */
    @TableField("category")
    private String category;

    /**
     * 商品类型：1=全量商品，2=积分商品
     */
    @TableField("type")
    private Integer type;

    /**
     * 商品价格（元）
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 积分价格
     */
    @TableField("points_price")
    private Integer pointsPrice;

    /**
     * 商品描述
     */
    @TableField("description")
    private String description;

    /**
     * 状态：1=上架，0=下架
     */
    @TableField("status")
    private Integer status;

    /**
     * 排序权重
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 是否删除：0=否，1=是
     */
    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
