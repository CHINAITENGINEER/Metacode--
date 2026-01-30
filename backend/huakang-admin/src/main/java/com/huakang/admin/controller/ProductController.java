package com.huakang.admin.controller;

import com.huakang.common.core.PageResult;
import com.huakang.common.core.Result;
import com.huakang.service.annotation.RequireRole;
import com.huakang.service.dto.product.CreateProductDTO;
import com.huakang.service.dto.product.ProductListDTO;
import com.huakang.service.dto.product.ProductVO;
import com.huakang.service.dto.product.UpdateProductDTO;
import com.huakang.service.service.OssService;
import com.huakang.service.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 商品管理控制器
 *
 * @author huakang
 */
@Tag(name = "商品管理", description = "商品CRUD、图片上传接口")
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@RequireRole("admin")  // 仅管理员可访问
public class ProductController {

    private final ProductService productService;
    private final OssService ossService;

    /**
     * 分页查询商品列表
     */
    @Operation(summary = "商品列表", description = "分页查询商品列表，支持按类型、分类、名称、状态筛选")
    @GetMapping
    public Result<PageResult<ProductVO>> listProducts(ProductListDTO queryDTO) {
        PageResult<ProductVO> result = productService.listProducts(queryDTO);
        return Result.success(result);
    }

    /**
     * 获取商品详情
     */
    @Operation(summary = "商品详情", description = "根据ID获取商品详细信息")
    @GetMapping("/{id}")
    public Result<ProductVO> getProduct(@PathVariable Long id) {
        ProductVO product = productService.getProductById(id);
        return Result.success(product);
    }

    /**
     * 创建商品
     */
    @Operation(summary = "创建商品", description = "创建新商品")
    @PostMapping
    public Result<ProductVO> createProduct(@Valid @RequestBody CreateProductDTO createDTO) {
        ProductVO product = productService.createProduct(createDTO);
        return Result.success("创建成功", product);
    }

    /**
     * 更新商品
     */
    @Operation(summary = "更新商品", description = "更新商品信息")
    @PutMapping("/{id}")
    public Result<ProductVO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductDTO updateDTO) {
        ProductVO product = productService.updateProduct(id, updateDTO);
        return Result.success("更新成功", product);
    }

    /**
     * 下架商品
     */
    @Operation(summary = "下架商品", description = "下架商品（软删除）")
    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return Result.<Void>success("下架成功", null);
    }

    /**
     * 图片上传接口
     */
    @Operation(summary = "图片上传", description = "上传商品图片到OSS，返回图片URL")
    @PostMapping("/upload")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        String imageUrl = ossService.uploadImage(file, "products");
        return Result.success("上传成功", imageUrl);
    }
}
