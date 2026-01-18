package com.carbon.controller;

import com.carbon.common.Result;
import com.carbon.dto.PageResult;
import com.carbon.dto.ProductDTO;
import com.carbon.dto.ProductVO;
import com.carbon.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 商品控制器
 */
@RestController
@RequestMapping("/api/product")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    /**
     * 获取商品列表（用户端）
     */
    @GetMapping("/list")
    public Result<PageResult<ProductVO>> getProductList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "12") Integer size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        PageResult<ProductVO> result = productService.getProductList(page, size, category, keyword, status);
        return Result.success(result);
    }
    
    /**
     * 获取商品详情
     */
    @GetMapping("/{id}")
    public Result<ProductVO> getProductDetail(@PathVariable Long id) {
        ProductVO product = productService.getProductDetail(id);
        return Result.success(product);
    }
    
    /**
     * 创建商品（管理员）
     */
    @PostMapping
    public Result<Map<String, Long>> createProduct(@RequestBody ProductDTO dto) {
        Long productId = productService.createProduct(dto);
        Map<String, Long> data = new HashMap<>();
        data.put("id", productId);
        return Result.success("创建成功", data);
    }
    
    /**
     * 更新商品（管理员）
     */
    @PutMapping("/{id}")
    public Result<Void> updateProduct(@PathVariable Long id, @RequestBody ProductDTO dto) {
        productService.updateProduct(id, dto);
        return Result.success("更新成功", null);
    }
    
    /**
     * 删除商品（管理员）
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return Result.success("删除成功", null);
    }
    
    /**
     * 上架或下架商品（管理员）
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateProductStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        productService.updateProductStatus(id, status);
        return Result.success("操作成功", null);
    }
}
