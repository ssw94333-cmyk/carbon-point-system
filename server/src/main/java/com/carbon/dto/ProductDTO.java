package com.carbon.dto;

import lombok.Data;

/**
 * 商品创建/更新DTO
 */
@Data
public class ProductDTO {
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
}
