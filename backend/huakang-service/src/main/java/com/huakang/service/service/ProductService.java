package com.huakang.service.service;

import com.huakang.common.core.PageResult;
import com.huakang.service.dto.product.CreateProductDTO;
import com.huakang.service.dto.product.ProductListDTO;
import com.huakang.service.dto.product.ProductVO;
import com.huakang.service.dto.product.UpdateProductDTO;

/**
 * 商品服务接口
 *
 * @author huakang
 */
public interface ProductService {

    /**
     * 分页查询商品列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageResult<ProductVO> listProducts(ProductListDTO queryDTO);

    /**
     * 根据ID获取商品详情
     *
     * @param productId 商品ID
     * @return 商品信息
     */
    ProductVO getProductById(Long productId);

    /**
     * 创建商品
     *
     * @param createDTO 创建信息
     * @return 商品信息
     */
    ProductVO createProduct(CreateProductDTO createDTO);

    /**
     * 更新商品
     *
     * @param productId 商品ID
     * @param updateDTO 更新信息
     * @return 商品信息
     */
    ProductVO updateProduct(Long productId, UpdateProductDTO updateDTO);

    /**
     * 下架商品（软删除）
     *
     * @param productId 商品ID
     */
    void deleteProduct(Long productId);
}
