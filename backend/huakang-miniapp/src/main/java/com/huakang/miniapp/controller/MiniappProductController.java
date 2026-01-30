package com.huakang.miniapp.controller;

import com.huakang.common.core.PageResult;
import com.huakang.common.core.Result;
import com.huakang.service.dto.product.ProductListDTO;
import com.huakang.service.dto.product.ProductVO;
import com.huakang.service.service.ProductService;
import com.huakang.service.service.SystemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 小程序商品控制器
 *
 * @author huakang
 */
@Slf4j
@Tag(name = "小程序商品", description = "商品展示、搜索接口")
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class MiniappProductController {

    private final ProductService productService;
    private final SystemConfigService systemConfigService;

    @Operation(summary = "商品列表", description = "获取商品列表，支持分类、类型筛选")
    @GetMapping
    public Result<PageResult<ProductVO>> listProducts(ProductListDTO queryDTO) {
        // 设置默认查询条件：商品类型为1（全量商品）或2（积分商品）且状态为上架
        if (queryDTO.getType() == null) {
            queryDTO.setType(1); // 默认查询全量商品
        }
        if (queryDTO.getStatus() == null) {
            queryDTO.setStatus(1); // 默认只查询上架商品
        }
        if (queryDTO.getIsDeleted() == null) {
            queryDTO.setIsDeleted(0); // 默认不查询已删除商品
        }
        
        PageResult<ProductVO> result = productService.listProducts(queryDTO);
        return Result.success(result);
    }

    @Operation(summary = "获取商品详情", description = "获取单个商品的详细信息")
    @GetMapping("/{id}")
    public Result<ProductVO> getProduct(@PathVariable Long id) {
        ProductVO product = productService.getProductById(id);
        return Result.success(product);
    }

    @Operation(summary = "获取企业微信二维码", description = "获取后台上传的企业微信二维码，用于引导客户咨询")
    @GetMapping("/wechat-qrcode")
    public Result<String> getWechatQrCode() {
        return systemConfigService.getConfigByKey("wechat_qrcode_url") != null ? 
            Result.success(systemConfigService.getConfigByKey("wechat_qrcode_url").getConfigValue()) : 
            Result.success("");
    }
}