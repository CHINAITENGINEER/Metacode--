package com.huakang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huakang.common.core.PageResult;
import com.huakang.common.exception.BusinessException;
import com.huakang.mapper.ProductMapper;
import com.huakang.mapper.entity.Product;
import com.huakang.service.dto.product.CreateProductDTO;
import com.huakang.service.dto.product.ProductListDTO;
import com.huakang.service.dto.product.ProductVO;
import com.huakang.service.dto.product.UpdateProductDTO;
import com.huakang.service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 商品服务实现类
 *
 * @author huakang
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Cacheable(value = "products", key = "'list:' + #queryDTO.type + ':' + #queryDTO.category + ':' + #queryDTO.page + ':' + #queryDTO.size", unless = "#result == null")
    public PageResult<ProductVO> listProducts(ProductListDTO queryDTO) {
        Page<Product> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();

        // 处理逻辑删除过滤
        // 如果明确指定查询已删除商品（is_deleted=1），需要手动添加条件（会覆盖@TableLogic的自动过滤）
        if (queryDTO.getIsDeleted() != null) {
            wrapper.eq(Product::getIsDeleted, queryDTO.getIsDeleted());
        }
        // 如果未指定is_deleted，MyBatis-Plus的@TableLogic会自动添加is_deleted=0的条件

        // 构建查询条件
        if (queryDTO.getType() != null) {
            wrapper.eq(Product::getType, queryDTO.getType());
        }
        if (queryDTO.getCategory() != null && !queryDTO.getCategory().isEmpty()) {
            wrapper.eq(Product::getCategory, queryDTO.getCategory());
        }
        if (queryDTO.getName() != null && !queryDTO.getName().isEmpty()) {
            wrapper.like(Product::getName, queryDTO.getName());
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq(Product::getStatus, queryDTO.getStatus());
        }

        // 按排序权重和创建时间排序
        wrapper.orderByDesc(Product::getSortOrder)
                .orderByDesc(Product::getCreatedAt);

        Page<Product> result = productMapper.selectPage(page, wrapper);

        PageResult<ProductVO> pageResult = new PageResult<>();
        pageResult.setTotal(result.getTotal());
        pageResult.setPages(result.getPages());
        pageResult.setCurrent(result.getCurrent());
        pageResult.setSize(result.getSize());
        pageResult.setRecords(result.getRecords().stream()
                .map(this::convertToVO)
                .toList());

        return pageResult;
    }

    /**
     * 获取商品详情（带缓存）
     * 缓存key: products::id:{productId}
     * 过期时间: 5分钟（在RedisConfig中配置）
     */
    @Override
    @Cacheable(value = "products", key = "'id:' + #productId", unless = "#result == null")
    public ProductVO getProductById(Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        return convertToVO(product);
    }

    /**
     * 创建商品（清除列表缓存，新商品详情会自动缓存）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "products", allEntries = true)
    public ProductVO createProduct(CreateProductDTO createDTO) {
        // 验证积分商品必须设置积分价格
        if (createDTO.getType() != null && createDTO.getType() == 2) {
            if (createDTO.getPointsPrice() == null || createDTO.getPointsPrice() <= 0) {
                throw new BusinessException("积分商品必须设置积分价格");
            }
        }

        Product product = new Product();
        BeanUtils.copyProperties(createDTO, product);

        // 转换详情图片列表为JSON字符串
        if (createDTO.getDetailImages() != null && !createDTO.getDetailImages().isEmpty()) {
            try {
                product.setDetailImages(objectMapper.writeValueAsString(createDTO.getDetailImages()));
                
                // 如果主图为空，自动将详情图的第一张图片（非视频）设置为主图
                if (product.getImage() == null || product.getImage().isEmpty()) {
                    String firstImage = getFirstImageUrl(createDTO.getDetailImages());
                    if (firstImage != null) {
                        product.setImage(firstImage);
                    }
                }
            } catch (Exception e) {
                log.error("转换详情图片为JSON失败", e);
                throw new BusinessException("详情图片格式错误");
            }
        }

        productMapper.insert(product);
        return convertToVO(product);
    }

    /**
     * 更新商品（精确清除缓存：只清除该商品的缓存和列表缓存）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "products", key = "'id:' + #productId")
    public ProductVO updateProduct(Long productId, UpdateProductDTO updateDTO) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        // 验证积分商品必须设置积分价格
        if (updateDTO.getType() != null && updateDTO.getType() == 2) {
            if (updateDTO.getPointsPrice() == null || updateDTO.getPointsPrice() <= 0) {
                throw new BusinessException("积分商品必须设置积分价格");
            }
        }

        // 更新字段
        if (updateDTO.getName() != null) {
            product.setName(updateDTO.getName());
        }
        if (updateDTO.getImage() != null) {
            product.setImage(updateDTO.getImage());
        }
        if (updateDTO.getDetailImages() != null) {
            try {
                product.setDetailImages(objectMapper.writeValueAsString(updateDTO.getDetailImages()));
                
                // 如果主图为空，自动将详情图的第一张图片（非视频）设置为主图
                if (product.getImage() == null || product.getImage().isEmpty()) {
                    String firstImage = getFirstImageUrl(updateDTO.getDetailImages());
                    if (firstImage != null) {
                        product.setImage(firstImage);
                    }
                }
            } catch (Exception e) {
                log.error("转换详情图片为JSON失败", e);
                throw new BusinessException("详情图片格式错误");
            }
        }
        if (updateDTO.getCategory() != null) {
            product.setCategory(updateDTO.getCategory());
        }
        if (updateDTO.getType() != null) {
            product.setType(updateDTO.getType());
        }
        if (updateDTO.getPrice() != null) {
            product.setPrice(updateDTO.getPrice());
        }
        if (updateDTO.getPointsPrice() != null) {
            product.setPointsPrice(updateDTO.getPointsPrice());
        }
        if (updateDTO.getDescription() != null) {
            product.setDescription(updateDTO.getDescription());
        }
        if (updateDTO.getStatus() != null) {
            product.setStatus(updateDTO.getStatus());
        }
        if (updateDTO.getSortOrder() != null) {
            product.setSortOrder(updateDTO.getSortOrder());
        }

        productMapper.updateById(product);
        return convertToVO(product);
    }

    /**
     * 下架商品（设置is_deleted=1 和 status=0）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "products", allEntries = true)
    public void deleteProduct(Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        // 下架商品：设置is_deleted=1（逻辑删除）和status=0（下架）
        product.setIsDeleted(1);
        product.setStatus(0);
        productMapper.updateById(product);
    }

    /**
     * 上架商品（恢复商品，设置is_deleted=0 和 status=1）
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "products", allEntries = true)
    public void onlineProduct(Long productId) {
        // 直接更新is_deleted和status字段，因为MyBatis-Plus的逻辑删除会过滤已删除记录
        Product product = new Product();
        product.setId(productId);
        product.setIsDeleted(0);
        product.setStatus(1);
        int result = productMapper.updateById(product);
        if (result == 0) {
            throw new BusinessException("商品不存在或已是上架状态");
        }
    }

    /**
     * 转换为VO
     */
    private ProductVO convertToVO(Product product) {
        ProductVO vo = new ProductVO();
        BeanUtils.copyProperties(product, vo);

        // 转换详情图片JSON字符串为List
        if (product.getDetailImages() != null && !product.getDetailImages().isEmpty()) {
            try {
                List<String> detailImages = objectMapper.readValue(
                        product.getDetailImages(),
                        new TypeReference<List<String>>() {}
                );
                vo.setDetailImages(detailImages);
                
                // 如果主图为空，自动将详情图的第一张图片（非视频）设置为主图
                if (vo.getImage() == null || vo.getImage().isEmpty()) {
                    String firstImage = getFirstImageUrl(detailImages);
                    if (firstImage != null) {
                        vo.setImage(firstImage);
                    }
                }
            } catch (Exception e) {
                log.warn("解析详情图片JSON失败: {}", e.getMessage());
                vo.setDetailImages(List.of());
            }
        } else {
            vo.setDetailImages(List.of());
        }

        return vo;
    }

    /**
     * 从URL列表中获取第一张图片URL（排除视频）
     */
    private String getFirstImageUrl(List<String> urls) {
        if (urls == null || urls.isEmpty()) {
            return null;
        }
        
        for (String url : urls) {
            if (url != null && !url.isEmpty() && !isVideoUrl(url)) {
                return url;
            }
        }
        
        return null;
    }

    /**
     * 判断URL是否为视频
     */
    private boolean isVideoUrl(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }
        // 根据文件扩展名判断是否为视频
        String lowerUrl = url.toLowerCase();
        return lowerUrl.endsWith(".mp4") || 
               lowerUrl.endsWith(".mov") || 
               lowerUrl.endsWith(".avi") || 
               lowerUrl.endsWith(".wmv") || 
               lowerUrl.endsWith(".mpeg");
    }
}
