package com.carbon.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品视图对象
 */
@Data
public class ProductVO {
    private Long id;
    
    private String name;
    
    private String category;
    
    private String description;
    
    private String image;
    
    private Integer pointsRequired;
    
    private Integer stock;
    
    private Integer totalStock;
    
    private Integer exchangeLimit;
    
    private Integer status;
    
    private Integer sortOrder;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
