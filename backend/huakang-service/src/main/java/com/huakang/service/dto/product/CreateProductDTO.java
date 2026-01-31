package com.huakang.service.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "创建商品请求参数")
public class CreateProductDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品名称
     */
    @Schema(description = "商品名称", example = "华康电器洗衣机", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "商品名称不能为空")
    private String name;

    /**
     * 商品主图URL
     */
    @Schema(description = "商品主图URL", example = "https://example.com/product.jpg")
    private String image;

    /**
     * 商品详情图URL列表
     */
    @Schema(description = "商品详情图URL列表", example = "[\"https://example.com/detail1.jpg\", \"https://example.com/detail2.jpg\"]")
    private List<String> detailImages;

    /**
     * 商品分类
     */
    @Schema(description = "商品分类", example = "家用电器")
    private String category;

    /**
     * 商品类型
     * - 1: 全量商品（可用现金购买）
     * - 2: 积分商品（只能用积分兑换）
     */
    @Schema(description = "商品类型（1=全量商品，2=积分商品）", 
            example = "1",
            allowableValues = {"1", "2"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "商品类型不能为空")
    private Integer type;

    /**
     * 商品价格（元）
     * 仅全量商品需要填写
     */
    @Schema(description = "商品价格（元，全量商品必填）", example = "2999.00")
    @Positive(message = "商品价格必须大于0")
    private BigDecimal price;

    /**
     * 积分价格
     * 积分商品必填，表示兑换所需积分数量
     */
    @Schema(description = "积分价格（积分商品必填）", example = "1000")
    private Integer pointsPrice;

    /**
     * 商品描述
     */
    @Schema(description = "商品描述", example = "高品质洗衣机，节能环保")
    private String description;

    /**
     * 状态
     * - 1: 上架（用户可见可购买）
     * - 0: 下架（用户不可见）
     */
    @Schema(description = "状态（1=上架，0=下架）", example = "1", allowableValues = {"0", "1"})
    private Integer status = 1;

    /**
     * 排序权重
     * 数值越大，排序越靠前
     */
    @Schema(description = "排序权重（数值越大越靠前）", example = "0")
    private Integer sortOrder = 0;
}
