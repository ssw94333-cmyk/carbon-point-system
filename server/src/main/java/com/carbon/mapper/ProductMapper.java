package com.carbon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.carbon.dto.ProductQueryDTO;
import com.carbon.dto.ProductVO;
import com.carbon.model.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 商品Mapper接口
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    
    /**
     * 查询商品列表
     */
    List<ProductVO> selectProductList(@Param("query") ProductQueryDTO query, @Param("offset") Integer offset, @Param("size") Integer size);
    
    /**
     * 统计商品数量
     */
    Integer countProducts(@Param("query") ProductQueryDTO query);
    
    /**
     * 根据ID查询商品详情
     */
    ProductVO selectProductById(@Param("id") Long id);
    
    /**
     * 更新商品库存
     */
    @Update("UPDATE product SET stock = stock - #{quantity}, update_time = NOW() WHERE id = #{id} AND stock >= #{quantity}")
    int decreaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);
    
    /**
     * 恢复商品库存
     */
    @Update("UPDATE product SET stock = stock + #{quantity}, update_time = NOW() WHERE id = #{id}")
    void increaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);
    
    /**
     * 更新商品状态
     */
    @Update("UPDATE product SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    void updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
