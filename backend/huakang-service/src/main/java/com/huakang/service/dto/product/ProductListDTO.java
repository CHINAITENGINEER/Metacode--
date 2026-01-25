package com.huakang.service.dto.product;

import lombok.Data;

import java.io.Serializable;

/**
 * 商品列表查询DTO
 *
 * @author huakang
 */
@Data
public class ProductListDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 页码（从1开始）
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;

    /**
     * 商品类型：1=全量商品，2=积分商品（可选）
     */
    private Integer type;

    /**
     * 商品分类（可选）
     */
    private String category;

    /**
     * 商品名称（模糊查询，可选）
     */
    private String name;

    /**
     * 状态：1=上架，0=下架（可选）
     */
    private Integer status;
}
