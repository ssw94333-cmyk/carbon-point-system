package com.carbon.dto;

import lombok.Data;

/**
 * 商品查询DTO
 */
@Data
public class ProductQueryDTO {
    private String category;
    
    private Integer minPoints;
    
    private Integer maxPoints;
    
    private String keyword;
    
    private Integer status;
}
