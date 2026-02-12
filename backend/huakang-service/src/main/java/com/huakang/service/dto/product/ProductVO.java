package com.huakang.service.dto.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "商品信息")
public class ProductVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "商品ID", example = "1")
    @JsonProperty("id")
    private Long id;

    @Schema(description = "商品名称", example = "华康电器洗衣机")
    private String name;

    @Schema(description = "商品主图URL", example = "https://example.com/product.jpg")
    private String image;

    @Schema(description = "商品详情图URL列表", example = "[\"https://example.com/detail1.jpg\"]")
    private List<String> detailImages;

    @Schema(description = "商品分类", example = "家用电器")
    private String category;

    @Schema(description = "商品类型（1=全量商品，2=积分商品）", example = "1")
    private Integer type;

    @Schema(description = "商品价格（元）", example = "2999.00")
    private BigDecimal price;

    @Schema(description = "积分价格（积分商品时有值）", example = "1000")
    private Integer pointsPrice;

    @Schema(description = "商品描述", example = "高品质洗衣机，节能环保")
    private String description;

    @Schema(description = "状态（1=上架，0=下架）", example = "1")
    private Integer status;

    @Schema(description = "排序权重（数值越大越靠前）", example = "0")
    private Integer sortOrder;

    @Schema(description = "创建时间", example = "2025-01-01T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间", example = "2025-01-01T10:00:00")
    private LocalDateTime updatedAt;
}
