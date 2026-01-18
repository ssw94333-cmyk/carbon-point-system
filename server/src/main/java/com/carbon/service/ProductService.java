package com.carbon.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.carbon.dto.PageResult;
import com.carbon.dto.ProductDTO;
import com.carbon.dto.ProductQueryDTO;
import com.carbon.dto.ProductVO;
import com.carbon.mapper.ExchangeOrderMapper;
import com.carbon.mapper.ProductMapper;
import com.carbon.model.Product;
import com.carbon.util.UserContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 商品服务类
 */
@Service
public class ProductService {
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private ExchangeOrderMapper exchangeOrderMapper;
    
    /**
     * 获取商品列表（用户端）
     */
    public PageResult<ProductVO> getProductList(Integer page, Integer size, String category, String keyword, Integer status) {
        ProductQueryDTO query = new ProductQueryDTO();
        // 如果status参数为null，不设置状态过滤，查询所有商品
        // 如果status参数有值，按指定状态过滤
        query.setStatus(status);
        query.setCategory(category);
        query.setKeyword(keyword);
        
        int offset = (page - 1) * size;
        List<ProductVO> list = productMapper.selectProductList(query, offset, size);
        Integer total = productMapper.countProducts(query);
        
        return new PageResult<>(list, total.longValue(), page, size);
    }
    
    /**
     * 获取商品详情
     */
    public ProductVO getProductDetail(Long id) {
        ProductVO product = productMapper.selectProductById(id);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        return product;
    }
    
    /**
     * 创建商品（管理员）
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createProduct(ProductDTO dto) {
        // 检查是否为管理员
        if (UserContext.getUserType() != 1) {
            throw new RuntimeException("无权限操作");
        }
        
        Product product = new Product();
        BeanUtils.copyProperties(dto, product);
        
        // 设置默认值
        if (product.getStatus() == null) {
            product.setStatus(1);
        }
        if (product.getSortOrder() == null) {
            product.setSortOrder(0);
        }
        if (product.getExchangeLimit() == null) {
            product.setExchangeLimit(0);
        }
        
        productMapper.insert(product);
        return product.getId();
    }
    
    /**
     * 更新商品（管理员）
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateProduct(Long id, ProductDTO dto) {
        // 检查是否为管理员
        if (UserContext.getUserType() != 1) {
            throw new RuntimeException("无权限操作");
        }
        
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        
        BeanUtils.copyProperties(dto, product);
        product.setId(id);
        productMapper.updateById(product);
    }
    
    /**
     * 删除商品（管理员）
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(Long id) {
        // 检查是否为管理员
        if (UserContext.getUserType() != 1) {
            throw new RuntimeException("无权限操作");
        }
        
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        
        // 检查是否有兑换记录
        LambdaQueryWrapper<com.carbon.model.ExchangeOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(com.carbon.model.ExchangeOrder::getProductId, id);
        Long count = exchangeOrderMapper.selectCount(wrapper);
        if (count > 0) {
            throw new RuntimeException("该商品已有兑换记录，不允许删除");
        }
        
        productMapper.deleteById(id);
    }
    
    /**
     * 上架或下架商品（管理员）
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateProductStatus(Long id, Integer status) {
        // 检查是否为管理员
        if (UserContext.getUserType() != 1) {
            throw new RuntimeException("无权限操作");
        }
        
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        
        productMapper.updateStatus(id, status);
    }
    
    /**
     * 获取商品列表（管理员）
     */
    public PageResult<ProductVO> getAdminProductList(Integer page, Integer size, String category, Integer status, String keyword) {
        // 检查是否为管理员
        if (UserContext.getUserType() != 1) {
            throw new RuntimeException("无权限操作");
        }
        
        ProductQueryDTO query = new ProductQueryDTO();
        query.setStatus(status);
        query.setCategory(category);
        query.setKeyword(keyword);
        
        int offset = (page - 1) * size;
        List<ProductVO> list = productMapper.selectProductList(query, offset, size);
        Integer total = productMapper.countProducts(query);
        
        return new PageResult<>(list, total.longValue(), page, size);
    }
}
